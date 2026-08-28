package com.shyam.payment.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.payment.dto.request.*;
import com.shyam.payment.dto.response.PaymentHistoryResponseDTO;
import com.shyam.payment.dto.response.PaymentResponseDTO;
import com.shyam.payment.dto.response.PaymentVerificationResponseDTO;
import com.shyam.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Payment", description = "Payment processing endpoints")
public class PaymentController {

  private final PaymentService paymentService;

  @Operation(summary = "Create payment", description = "Creates a payment request for an order.")
  @PostMapping("/create")
  public BaseResponseDTO<PaymentResponseDTO> createPayment(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @Valid @RequestBody CreatePaymentRequestDTO request) {
    log.info("Received payment create request for orderId={}", request.getOrderId());
    return new BaseResponseDTO<>(paymentService.createPayment(request, idempotencyKey), null);
  }

  @Operation(
      summary = "Verify payment",
      description = "Verifies a gateway payment signature and updates payment/order status.")
  @PostMapping("/verify")
  public BaseResponseDTO<PaymentVerificationResponseDTO> verifyPayment(
      @Valid @RequestBody VerifyPaymentRequestDTO request) {
    log.info("Received payment verify request for paymentId={}", request.getPaymentId());
    return new BaseResponseDTO<>(paymentService.verifyPayment(request), null);
  }

  @Operation(summary = "Handle payment failure", description = "Marks an active payment as failed.")
  @PostMapping("/failure")
  public BaseResponseDTO<PaymentResponseDTO> handleFailure(
      @RequestBody PaymentFailureRequestDTO request) {
    log.info("Received payment failure request for paymentId={}", request.getPaymentId());
    return new BaseResponseDTO<>(paymentService.handleFailure(request), null);
  }

  @Operation(summary = "Retry payment", description = "Creates a new payment attempt for an order.")
  @PostMapping("/{paymentId}/retry")
  public BaseResponseDTO<PaymentResponseDTO> retryPayment(
      @PathVariable Long paymentId,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @Valid @RequestBody RetryPaymentRequestDTO request) {
    log.info("Received payment retry request for paymentId={}", paymentId);
    return new BaseResponseDTO<>(
        paymentService.retryPayment(paymentId, request, idempotencyKey), null);
  }

  @Operation(summary = "Cancel payment", description = "Cancels an active payment request.")
  @PostMapping("/{paymentId}/cancel")
  public BaseResponseDTO<PaymentResponseDTO> cancelPayment(
      @PathVariable Long paymentId, @RequestBody CancelPaymentRequestDTO request) {
    log.info("Received payment cancel request for paymentId={}", paymentId);
    return new BaseResponseDTO<>(paymentService.cancelPayment(paymentId, request), null);
  }

  @Operation(summary = "Refund payment", description = "Refunds a successful payment.")
  @PostMapping("/refund")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<PaymentResponseDTO> refundPayment(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @Valid @RequestBody RefundPaymentRequestDTO request) {
    log.info("Received payment refund request for paymentId={}", request.getPaymentId());
    return new BaseResponseDTO<>(paymentService.refundPayment(request, idempotencyKey), null);
  }

  @Operation(summary = "Get payment", description = "Gets payment details by payment id.")
  @GetMapping("/{paymentId}")
  public BaseResponseDTO<PaymentResponseDTO> getPayment(@PathVariable Long paymentId) {
    return new BaseResponseDTO<>(paymentService.getPayment(paymentId), null);
  }

  @Operation(
      summary = "Get payment history",
      description = "Gets all payment attempts for an order.")
  @GetMapping("/orders/{orderId}/history")
  public BaseResponseDTO<PaymentHistoryResponseDTO> getPaymentHistory(@PathVariable Long orderId) {
    return new BaseResponseDTO<>(paymentService.getPaymentHistory(orderId), null);
  }
}
