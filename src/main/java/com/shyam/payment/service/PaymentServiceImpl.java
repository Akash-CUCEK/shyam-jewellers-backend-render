package com.shyam.payment.service;

import com.shyam.common.constants.*;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dao.OrderDAO;
import com.shyam.dto.response.GetOrderInvoiceResponse;
import com.shyam.entity.Order;
import com.shyam.payment.config.PaymentProperties;
import com.shyam.payment.dto.request.*;
import com.shyam.payment.dto.response.PaymentHistoryResponseDTO;
import com.shyam.payment.dto.response.PaymentResponseDTO;
import com.shyam.payment.dto.response.PaymentVerificationResponseDTO;
import com.shyam.payment.entity.Payment;
import com.shyam.payment.entity.PaymentAudit;
import com.shyam.payment.entity.PaymentGatewayResponse;
import com.shyam.payment.entity.PaymentTransaction;
import com.shyam.payment.event.PaymentEvent;
import com.shyam.payment.event.PaymentEventPublisher;
import com.shyam.payment.gateway.*;
import com.shyam.payment.mapper.PaymentMapper;
import com.shyam.payment.repository.PaymentAuditRepository;
import com.shyam.payment.repository.PaymentGatewayResponseRepository;
import com.shyam.payment.repository.PaymentRepository;
import com.shyam.payment.repository.PaymentTransactionRepository;
import com.shyam.repository.OrderRepository;
import com.shyam.service.InvoiceService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  private static final Set<PaymentStatus> ACTIVE_PAYMENT_STATUSES =
      EnumSet.of(PaymentStatus.CREATED, PaymentStatus.PENDING, PaymentStatus.PROCESSING);

  private final OrderDAO orderDAO;
  private final OrderRepository orderRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final PaymentGatewayResponseRepository paymentGatewayResponseRepository;
  private final PaymentAuditRepository paymentAuditRepository;
  private final PaymentGatewayProviderFactory gatewayProviderFactory;
  private final PaymentProperties paymentProperties;
  private final InvoiceService invoiceService;
  private final PaymentEventPublisher paymentEventPublisher;
  private final PaymentMapper paymentMapper;

  @Override
  @Transactional
  public PaymentResponseDTO createPayment(
      CreatePaymentRequestDTO request, String idempotencyKeyHeader) {
    String idempotencyKey =
        resolveRequiredIdempotencyKey(request.getIdempotencyKey(), idempotencyKeyHeader);

    Payment existing = findByIdempotencyKey(idempotencyKey);
    if (existing != null) {
      expirePaymentIfNeeded(existing);
      return paymentMapper.toResponse(existing);
    }

    Order order = orderDAO.findOrderByOrderId(request.getOrderId());
    preventDuplicateSuccessfulPayment(order.getId());

    Payment activePayment =
        paymentRepository
            .findFirstByOrder_IdAndStatusInOrderByCreatedAtDesc(
                order.getId(), ACTIVE_PAYMENT_STATUSES)
            .orElse(null);
    if (activePayment != null) {
      expirePaymentIfNeeded(activePayment);
      if (activePayment.isActive()) {
        return paymentMapper.toResponse(activePayment);
      }
    }

    BigDecimal payableAmount = resolvePayableAmount(order, request.getAmount());
    PaymentGateway gateway = resolveGateway(request.getGateway());
    LocalDateTime expiresAt =
        LocalDateTime.now().plusMinutes(paymentProperties.getRequestTtlMinutes());

    Payment payment =
        Payment.builder()
            .paymentReference(newPaymentReference())
            .order(order)
            .amount(payableAmount)
            .currency(paymentProperties.getCurrency())
            .status(PaymentStatus.CREATED)
            .paymentMethod(request.getPaymentMethod())
            .gateway(gateway)
            .idempotencyKey(idempotencyKey)
            .expiresAt(expiresAt)
            .build();

    payment = paymentRepository.save(payment);
    audit(
        payment, "PAYMENT_CREATED", null, PaymentStatus.CREATED, "SYSTEM", "Payment created", null);

    PaymentGatewayProvider provider = gatewayProviderFactory.getProvider(gateway);
    GatewayCreatePaymentResponse gatewayResponse =
        provider.createPayment(
            new GatewayCreatePaymentRequest(
                payment.getPaymentReference(),
                order.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                paymentProperties.getUrls().getCallbackUrl(),
                paymentProperties.getUrls().getSuccessUrl(),
                paymentProperties.getUrls().getFailureUrl(),
                expiresAt));

    payment.setGatewayOrderId(gatewayResponse.gatewayOrderId());
    payment.setGatewayReferenceId(gatewayResponse.gatewayReferenceId());
    payment.setPaymentUrl(gatewayResponse.paymentUrl());
    transitionPayment(payment, PaymentStatus.PENDING, "Payment request generated");
    recordTransaction(
        payment,
        PaymentTransactionType.CREATE,
        PaymentStatus.PENDING,
        gatewayResponse.gatewayOrderId(),
        gatewayResponse.gatewayReferenceId(),
        idempotencyKey,
        null,
        gatewayResponse.rawResponse());
    saveGatewayResponse(
        payment,
        "CREATE",
        PaymentStatus.PENDING,
        gatewayResponse.gatewayOrderId(),
        null,
        null,
        null,
        gatewayResponse.rawResponse());

    order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
    order.setPaymentStatus(OrderPaymentStatus.PENDING);
    order.setPaymentMethod(request.getPaymentMethod());
    orderRepository.save(order);

    payment = paymentRepository.save(payment);
    publish(payment);
    return paymentMapper.toResponse(payment);
  }

  @Override
  @Transactional
  public PaymentVerificationResponseDTO verifyPayment(VerifyPaymentRequestDTO request) {
    Payment payment =
        resolvePaymentForUpdate(request.getPaymentId(), request.getPaymentReference());
    expirePaymentIfNeeded(payment);

    if (payment.getStatus() == PaymentStatus.SUCCESS) {
      return successResponse(payment, invoiceService.generateInvoice(payment.getOrder()));
    }

    if (payment.getStatus() == PaymentStatus.EXPIRED) {
      throw paymentConflict(
          "Payment request has expired", "Payment " + payment.getId() + " expired");
    }

    validateGatewayOrder(payment, request.getGatewayOrderId());
    validateAmount(payment, request.getAmount());
    preventDuplicateGatewayPayment(payment, request.getGatewayPaymentId());

    PaymentGatewayProvider provider = gatewayProviderFactory.getProvider(payment.getGateway());
    GatewayVerificationResult result =
        provider.verifyPayment(
            payment,
            new GatewayPaymentVerificationRequest(
                request.getGatewayOrderId(),
                request.getGatewayPaymentId(),
                request.getSignature(),
                request.getStatus(),
                request.getAmount(),
                request.getRawResponse()));

    saveGatewayResponse(
        payment,
        "VERIFY",
        result.status(),
        request.getGatewayOrderId(),
        result.gatewayPaymentId(),
        request.getSignature(),
        result.signatureValid(),
        result.rawResponse());

    if (!result.signatureValid()) {
      audit(
          payment,
          "SIGNATURE_REJECTED",
          payment.getStatus(),
          payment.getStatus(),
          "GATEWAY",
          "Payment signature verification failed",
          result.rawResponse());
      throw badRequest(
          "Invalid payment signature", "Signature rejected for payment " + payment.getId());
    }

    if (isGatewaySuccess(result.status())) {
      return markPaymentSuccess(payment, result);
    }

    if (isGatewayFailure(result.status())) {
      markPaymentFailed(
          payment,
          result.failureCode(),
          result.failureReason() != null ? result.failureReason() : "Payment failed",
          result.rawResponse());
      return verificationResponse(payment, false, null);
    }

    transitionPayment(payment, PaymentStatus.PROCESSING, "Payment verification is processing");
    recordTransaction(
        payment,
        PaymentTransactionType.VERIFY,
        PaymentStatus.PROCESSING,
        request.getGatewayOrderId(),
        result.gatewayPaymentId(),
        null,
        true,
        result.rawResponse());
    paymentRepository.save(payment);
    publish(payment);
    return verificationResponse(payment, false, null);
  }

  @Override
  @Transactional
  public PaymentResponseDTO handleFailure(PaymentFailureRequestDTO request) {
    Payment payment =
        resolvePaymentForUpdate(request.getPaymentId(), request.getPaymentReference());

    if (payment.getStatus() == PaymentStatus.SUCCESS) {
      throw paymentConflict(
          "Successful payment cannot be marked as failed",
          "Payment " + payment.getId() + " is already successful");
    }

    if (payment.getStatus() == PaymentStatus.FAILED) {
      return paymentMapper.toResponse(payment);
    }

    saveGatewayResponse(
        payment,
        "FAILURE",
        PaymentStatus.FAILED,
        request.getGatewayOrderId(),
        request.getGatewayPaymentId(),
        null,
        null,
        request.getRawResponse());
    markPaymentFailed(
        payment, request.getFailureCode(), request.getFailureReason(), request.getRawResponse());
    return paymentMapper.toResponse(payment);
  }

  @Override
  @Transactional
  public PaymentResponseDTO retryPayment(
      Long paymentId, RetryPaymentRequestDTO request, String idempotencyKeyHeader) {
    Payment previousPayment = resolvePaymentForUpdate(paymentId, null);
    expirePaymentIfNeeded(previousPayment);

    if (previousPayment.getStatus() == PaymentStatus.SUCCESS) {
      throw paymentConflict(
          "Payment is already successful", "Payment " + paymentId + " does not need retry");
    }

    if (previousPayment.isActive()) {
      return paymentMapper.toResponse(previousPayment);
    }

    audit(
        previousPayment,
        "PAYMENT_RETRY_REQUESTED",
        previousPayment.getStatus(),
        previousPayment.getStatus(),
        "SYSTEM",
        "Retry requested",
        null);

    CreatePaymentRequestDTO retryRequest =
        CreatePaymentRequestDTO.builder()
            .orderId(previousPayment.getOrder().getId())
            .paymentMethod(
                request.getPaymentMethod() != null
                    ? request.getPaymentMethod()
                    : previousPayment.getPaymentMethod())
            .gateway(
                request.getGateway() != null ? request.getGateway() : previousPayment.getGateway())
            .amount(request.getAmount())
            .idempotencyKey(request.getIdempotencyKey())
            .build();

    return createPayment(retryRequest, idempotencyKeyHeader);
  }

  @Override
  @Transactional
  public PaymentResponseDTO cancelPayment(Long paymentId, CancelPaymentRequestDTO request) {
    Payment payment = resolvePaymentForUpdate(paymentId, null);
    expirePaymentIfNeeded(payment);

    if (payment.getStatus() == PaymentStatus.CANCELLED) {
      return paymentMapper.toResponse(payment);
    }

    if (!payment.isActive()) {
      throw paymentConflict(
          "Only active payments can be cancelled",
          "Payment " + paymentId + " is " + payment.getStatus());
    }

    GatewayCancelResult result =
        gatewayProviderFactory
            .getProvider(payment.getGateway())
            .cancelPayment(payment, request.getReason());

    transitionPayment(payment, result.status(), result.failureReason());
    payment.setCancelledAt(LocalDateTime.now());
    payment.getOrder().setOrderStatus(OrderStatus.PAYMENT_FAILED);
    payment.getOrder().setPaymentStatus(OrderPaymentStatus.FAILED);
    orderRepository.save(payment.getOrder());
    recordTransaction(
        payment,
        PaymentTransactionType.CANCEL,
        result.status(),
        payment.getGatewayOrderId(),
        payment.getGatewayPaymentId(),
        null,
        null,
        result.rawResponse());
    paymentRepository.save(payment);
    publish(payment);
    return paymentMapper.toResponse(payment);
  }

  @Override
  @Transactional
  public PaymentResponseDTO refundPayment(
      RefundPaymentRequestDTO request, String idempotencyKeyHeader) {
    String idempotencyKey =
        resolveRequiredIdempotencyKey(request.getIdempotencyKey(), idempotencyKeyHeader);
    Payment payment = resolvePaymentForUpdate(request.getPaymentId(), null);

    PaymentTransaction duplicateRefund =
        paymentTransactionRepository
            .findByPayment_IdAndIdempotencyKey(payment.getId(), idempotencyKey)
            .orElse(null);
    if (duplicateRefund != null) {
      return paymentMapper.toResponse(payment);
    }

    if (payment.getStatus() != PaymentStatus.SUCCESS) {
      throw paymentConflict(
          "Only successful payments can be refunded",
          "Payment " + payment.getId() + " is " + payment.getStatus());
    }

    BigDecimal refundAmount =
        request.getAmount() != null ? request.getAmount() : payment.getAmount();
    if (refundAmount.compareTo(payment.getAmount()) > 0) {
      throw badRequest(
          "Refund amount cannot exceed payment amount",
          "Refund amount " + refundAmount + " exceeds " + payment.getAmount());
    }

    transitionPayment(payment, PaymentStatus.REFUND_INITIATED, "Refund initiated");
    recordTransaction(
        payment,
        PaymentTransactionType.REFUND,
        PaymentStatus.REFUND_INITIATED,
        payment.getGatewayOrderId(),
        payment.getGatewayPaymentId(),
        idempotencyKey,
        null,
        request.getReason());

    GatewayRefundResult result =
        gatewayProviderFactory
            .getProvider(payment.getGateway())
            .refundPayment(payment, refundAmount, request.getReason(), idempotencyKey);

    if (result.status() == PaymentStatus.REFUNDED) {
      transitionPayment(payment, PaymentStatus.REFUNDED, "Refund completed");
      payment.setGatewayReferenceId(result.gatewayRefundId());
      payment.setRefundedAt(LocalDateTime.now());
      payment.getOrder().setOrderStatus(OrderStatus.REFUNDED);
      payment.getOrder().setPaymentStatus(OrderPaymentStatus.REFUNDED);
      orderRepository.save(payment.getOrder());
    }

    recordTransaction(
        payment,
        PaymentTransactionType.REFUND,
        result.status(),
        payment.getGatewayOrderId(),
        result.gatewayRefundId(),
        idempotencyKey,
        null,
        result.rawResponse());
    paymentRepository.save(payment);
    publish(payment);
    return paymentMapper.toResponse(payment);
  }

  @Override
  @Transactional
  public PaymentResponseDTO getPayment(Long paymentId) {
    Payment payment =
        paymentRepository
            .findById(paymentId)
            .orElseThrow(
                () ->
                    notFound(
                        "Payment does not exist", "Payment with id " + paymentId + " not found"));
    expirePaymentIfNeeded(payment);
    return paymentMapper.toResponse(payment);
  }

  @Override
  @Transactional
  public PaymentResponseDTO getLatestPaymentForOrder(Long orderId) {
    Payment payment =
        paymentRepository
            .findTopByOrder_IdOrderByCreatedAtDesc(orderId)
            .orElseThrow(
                () ->
                    notFound(
                        "Payment does not exist", "No payment found for order with id " + orderId));
    expirePaymentIfNeeded(payment);
    return paymentMapper.toResponse(payment);
  }

  @Override
  @Transactional
  public PaymentHistoryResponseDTO getPaymentHistory(Long orderId) {
    orderDAO.findOrderByOrderId(orderId);
    List<PaymentResponseDTO> payments =
        paymentRepository.findByOrder_IdOrderByCreatedAtDesc(orderId).stream()
            .peek(this::expirePaymentIfNeeded)
            .map(paymentMapper::toResponse)
            .toList();

    return PaymentHistoryResponseDTO.builder().orderId(orderId).payments(payments).build();
  }

  private PaymentVerificationResponseDTO markPaymentSuccess(
      Payment payment, GatewayVerificationResult result) {
    payment.setGatewayPaymentId(result.gatewayPaymentId());
    payment.setFailureCode(null);
    payment.setFailureReason(null);
    payment.setPaidAt(LocalDateTime.now());
    transitionPayment(payment, PaymentStatus.SUCCESS, "Payment verified successfully");

    Order order = payment.getOrder();
    order.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);
    order.setPaymentStatus(OrderPaymentStatus.PAID);
    order.setPaymentMethod(payment.getPaymentMethod());
    order.setDueAmount(BigDecimal.ZERO);
    orderRepository.save(order);

    recordTransaction(
        payment,
        PaymentTransactionType.VERIFY,
        PaymentStatus.SUCCESS,
        payment.getGatewayOrderId(),
        result.gatewayPaymentId(),
        null,
        true,
        result.rawResponse());
    paymentRepository.save(payment);

    GetOrderInvoiceResponse invoice = invoiceService.generateInvoice(order);
    publish(payment);
    return successResponse(payment, invoice);
  }

  private void markPaymentFailed(
      Payment payment, String failureCode, String failureReason, String rawResponse) {
    payment.setFailureCode(failureCode);
    payment.setFailureReason(
        failureReason != null && !failureReason.isBlank() ? failureReason : "Payment failed");
    payment.setFailedAt(LocalDateTime.now());
    transitionPayment(payment, PaymentStatus.FAILED, payment.getFailureReason());

    payment.getOrder().setOrderStatus(OrderStatus.PAYMENT_FAILED);
    payment.getOrder().setPaymentStatus(OrderPaymentStatus.FAILED);
    orderRepository.save(payment.getOrder());

    recordTransaction(
        payment,
        PaymentTransactionType.FAILURE,
        PaymentStatus.FAILED,
        payment.getGatewayOrderId(),
        payment.getGatewayPaymentId(),
        null,
        null,
        rawResponse);
    paymentRepository.save(payment);
    publish(payment);
  }

  private PaymentVerificationResponseDTO successResponse(
      Payment payment, GetOrderInvoiceResponse invoice) {
    return PaymentVerificationResponseDTO.builder()
        .payment(paymentMapper.toResponse(payment))
        .orderStatus(payment.getOrder().getOrderStatus().name())
        .orderPaymentStatus(payment.getOrder().getPaymentStatus().name())
        .invoiceGenerated(true)
        .invoiceFileName(invoice.getFileName())
        .invoicePdfBytes(invoice.getInvoicePdfBytes())
        .build();
  }

  private PaymentVerificationResponseDTO verificationResponse(
      Payment payment, boolean invoiceGenerated, GetOrderInvoiceResponse invoice) {
    return PaymentVerificationResponseDTO.builder()
        .payment(paymentMapper.toResponse(payment))
        .orderStatus(payment.getOrder().getOrderStatus().name())
        .orderPaymentStatus(payment.getOrder().getPaymentStatus().name())
        .invoiceGenerated(invoiceGenerated)
        .invoiceFileName(invoice != null ? invoice.getFileName() : null)
        .invoicePdfBytes(invoice != null ? invoice.getInvoicePdfBytes() : null)
        .build();
  }

  private Payment resolvePaymentForUpdate(Long paymentId, String paymentReference) {
    if (paymentId != null) {
      return paymentRepository
          .findByIdForUpdate(paymentId)
          .orElseThrow(
              () ->
                  notFound(
                      "Payment does not exist", "Payment with id " + paymentId + " not found"));
    }

    if (hasText(paymentReference)) {
      return paymentRepository
          .findByPaymentReferenceForUpdate(paymentReference)
          .orElseThrow(
              () ->
                  notFound(
                      "Payment does not exist",
                      "Payment with reference " + paymentReference + " not found"));
    }

    throw badRequest("Payment id or reference is required", "No payment identifier was provided");
  }

  private void transitionPayment(Payment payment, PaymentStatus nextStatus, String message) {
    PaymentStatus previousStatus = payment.getStatus();
    payment.setStatus(nextStatus);
    audit(payment, "STATUS_CHANGED", previousStatus, nextStatus, "SYSTEM", message, null);
  }

  private void expirePaymentIfNeeded(Payment payment) {
    if (!ACTIVE_PAYMENT_STATUSES.contains(payment.getStatus())) {
      return;
    }

    if (payment.getExpiresAt() == null || payment.getExpiresAt().isAfter(LocalDateTime.now())) {
      return;
    }

    transitionPayment(payment, PaymentStatus.EXPIRED, "Payment request expired");
    payment.getOrder().setOrderStatus(OrderStatus.PAYMENT_FAILED);
    payment.getOrder().setPaymentStatus(OrderPaymentStatus.FAILED);
    orderRepository.save(payment.getOrder());
    recordTransaction(
        payment,
        PaymentTransactionType.EXPIRE,
        PaymentStatus.EXPIRED,
        payment.getGatewayOrderId(),
        payment.getGatewayPaymentId(),
        null,
        null,
        "Payment request expired at " + payment.getExpiresAt());
    paymentRepository.save(payment);
    publish(payment);
  }

  private void recordTransaction(
      Payment payment,
      PaymentTransactionType type,
      PaymentStatus status,
      String gatewayOrderId,
      String gatewayTransactionId,
      String idempotencyKey,
      Boolean signatureVerified,
      String payload) {
    paymentTransactionRepository.save(
        PaymentTransaction.builder()
            .payment(payment)
            .transactionType(type)
            .status(status)
            .amount(payment.getAmount())
            .gatewayOrderId(gatewayOrderId)
            .gatewayTransactionId(gatewayTransactionId)
            .idempotencyKey(idempotencyKey)
            .signatureVerified(signatureVerified)
            .gatewayPayload(payload)
            .build());
  }

  private void saveGatewayResponse(
      Payment payment,
      String eventType,
      PaymentStatus status,
      String gatewayOrderId,
      String gatewayPaymentId,
      String signature,
      Boolean signatureValid,
      String rawPayload) {
    paymentGatewayResponseRepository.save(
        PaymentGatewayResponse.builder()
            .payment(payment)
            .gateway(payment.getGateway())
            .eventType(eventType)
            .status(status)
            .gatewayOrderId(gatewayOrderId)
            .gatewayPaymentId(gatewayPaymentId)
            .signature(signature)
            .signatureValid(signatureValid)
            .rawPayload(rawPayload)
            .build());
  }

  private void audit(
      Payment payment,
      String action,
      PaymentStatus fromStatus,
      PaymentStatus toStatus,
      String actor,
      String message,
      String metadata) {
    paymentAuditRepository.save(
        PaymentAudit.builder()
            .payment(payment)
            .action(action)
            .fromStatus(fromStatus)
            .toStatus(toStatus)
            .actor(actor)
            .message(message)
            .metadata(metadata)
            .build());
  }

  private void publish(Payment payment) {
    paymentEventPublisher.publish(
        new PaymentEvent(
            payment.getId(),
            payment.getPaymentReference(),
            payment.getOrder().getId(),
            payment.getStatus(),
            payment.getAmount(),
            payment.getCurrency(),
            LocalDateTime.now()));
  }

  private Payment findByIdempotencyKey(String idempotencyKey) {
    if (!hasText(idempotencyKey)) {
      return null;
    }
    return paymentRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
  }

  private void preventDuplicateSuccessfulPayment(Long orderId) {
    if (paymentRepository.existsByOrder_IdAndStatus(orderId, PaymentStatus.SUCCESS)) {
      throw paymentConflict(
          "Order is already paid", "A successful payment already exists for order " + orderId);
    }
  }

  private void preventDuplicateGatewayPayment(Payment payment, String gatewayPaymentId) {
    if (!hasText(gatewayPaymentId)) {
      throw badRequest("Gateway payment id is required", "gatewayPaymentId is blank");
    }

    if (gatewayPaymentId.equals(payment.getGatewayPaymentId())) {
      return;
    }

    if (paymentRepository.existsByGatewayPaymentId(gatewayPaymentId)) {
      throw paymentConflict(
          "Duplicate gateway payment id",
          "Gateway payment id " + gatewayPaymentId + " is already linked to another payment");
    }
  }

  private BigDecimal resolvePayableAmount(Order order, BigDecimal requestedAmount) {
    BigDecimal totalCost = order.getTotalCost();
    BigDecimal dueAmount = order.getDueAmount();
    BigDecimal payableAmount =
        dueAmount != null && dueAmount.compareTo(BigDecimal.ZERO) > 0 ? dueAmount : totalCost;

    if (payableAmount == null || payableAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw paymentConflict(
          "Order has no payable amount", "Order " + order.getId() + " is not payable");
    }

    if (requestedAmount != null && requestedAmount.compareTo(payableAmount) != 0) {
      throw badRequest(
          "Payment amount does not match order payable amount",
          "Expected " + payableAmount + " but received " + requestedAmount);
    }

    return payableAmount;
  }

  private void validateGatewayOrder(Payment payment, String gatewayOrderId) {
    if (!hasText(gatewayOrderId)) {
      throw badRequest("Gateway order id is required", "gatewayOrderId is blank");
    }

    if (!gatewayOrderId.equals(payment.getGatewayOrderId())) {
      throw badRequest(
          "Gateway order id does not match payment",
          "Expected " + payment.getGatewayOrderId() + " but received " + gatewayOrderId);
    }
  }

  private void validateAmount(Payment payment, BigDecimal amount) {
    if (amount != null && amount.compareTo(payment.getAmount()) != 0) {
      throw badRequest(
          "Gateway amount does not match payment amount",
          "Expected " + payment.getAmount() + " but received " + amount);
    }
  }

  private PaymentGateway resolveGateway(PaymentGateway gateway) {
    return gateway != null ? gateway : paymentProperties.getGateway().getProvider();
  }

  private boolean isGatewaySuccess(PaymentStatus status) {
    return status == PaymentStatus.SUCCESS || status == PaymentStatus.COMPLETED;
  }

  private boolean isGatewayFailure(PaymentStatus status) {
    return status == PaymentStatus.FAILED
        || status == PaymentStatus.CANCELLED
        || status == PaymentStatus.EXPIRED;
  }

  private String resolveRequiredIdempotencyKey(String requestKey, String headerKey) {
    String key = hasText(headerKey) ? headerKey : requestKey;
    if (!hasText(key)) {
      throw badRequest(
          "Idempotency key is required",
          "Provide Idempotency-Key header or idempotencyKey in the request body");
    }
    return key.trim();
  }

  private String newPaymentReference() {
    return "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private SYMException badRequest(String message, String detail) {
    return new SYMException(
        HttpStatus.BAD_REQUEST,
        SYMErrorType.USER_ERROR,
        ErrorCodeConstants.ERROR_CODE_VALIDATION,
        message,
        detail);
  }

  private SYMException paymentConflict(String message, String detail) {
    return new SYMException(
        HttpStatus.CONFLICT,
        SYMErrorType.USER_ERROR,
        ErrorCodeConstants.ERROR_CODE_VALIDATION,
        message,
        detail);
  }

  private SYMException notFound(String message, String detail) {
    return new SYMException(
        HttpStatus.NOT_FOUND,
        SYMErrorType.USER_ERROR,
        ErrorCodeConstants.ERROR_CODE_AUTHZ_USER_NOT_EXIST,
        message,
        detail);
  }
}
