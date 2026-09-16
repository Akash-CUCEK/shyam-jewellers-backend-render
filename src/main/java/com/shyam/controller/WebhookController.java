package com.shyam.controller;

import com.razorpay.Utils;
import com.shyam.common.constants.OrderStatus;
import com.shyam.common.constants.PaymentStatus;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.config.RazorpayConfig;
import com.shyam.entity.Order;
import com.shyam.entity.OrderItem;
import com.shyam.entity.Payment;
import com.shyam.entity.ProductVariant;
import com.shyam.repository.OrderRepository;
import com.shyam.repository.PaymentRepository;
import com.shyam.repository.ProductVariantRepository;
import com.shyam.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller for handling Razorpay webhooks. */
@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhook", description = "Endpoints for handling Razorpay webhooks")
public class WebhookController {

  private final OrderService orderService;
  private final PaymentRepository paymentRepository;
  private final RazorpayConfig razorpayConfig;
  private final OrderRepository orderRepository;
  private final ProductVariantRepository productVariantRepository;

  @Operation(
      summary = "Handle Razorpay webhook",
      description = "Process incoming Razorpay webhook events for payment status updates.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Webhook processed successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid signature or payload",
        content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/razorpay")
  public ResponseEntity<BaseResponseDTO<Void>> handleRazorpayWebhook(
      @RequestBody Map<String, Object> payload,
      @RequestHeader("X-Razorpay-Signature") String signature) {
    log.info("Received Razorpay webhook request");

    // Verify the signature using Razorpay secret
    boolean isValidSignature = false;
    try {
      // For webhook verification, we need the raw payload
      // Since we only have the parsed Map, we'll reconstruct it
      // Note: In production, you should configure Spring to expose the raw request body
      if (payload == null) {
        log.warn("Payload is null in webhook request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new BaseResponseDTO<>(null, null));
      }
      String payloadString = new JSONObject(payload).toString();
      isValidSignature =
          Utils.verifySignature(payloadString, signature, razorpayConfig.getKeySecret());
    } catch (Exception e) {
      // If there's any error in verification, treat as invalid signature
      log.warn("Error verifying webhook signature: {}", e.getMessage());
      isValidSignature = false;
    }

    if (!isValidSignature) {
      log.warn("Invalid signature in webhook request");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponseDTO<>(null, null));
    }

    // Parse the payload safely with null checks
    String event = (String) payload.get("event");
    if (event == null) {
      log.warn("Missing event in webhook payload");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponseDTO<>(null, null));
    }

    Map<String, Object> payloadData = (Map<String, Object>) payload.get("payload");
    if (payloadData == null) {
      log.warn("Missing payload in webhook");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponseDTO<>(null, null));
    }

    Map<String, Object> paymentEntity = (Map<String, Object>) payloadData.get("payment");
    if (paymentEntity == null) {
      log.warn("Missing payment in webhook payload");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponseDTO<>(null, null));
    }

    Map<String, Object> paymentDetails = (Map<String, Object>) paymentEntity.get("entity");
    if (paymentDetails == null) {
      log.warn("Missing payment entity in webhook payload");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponseDTO<>(null, null));
    }

    String razorpayPaymentId = (String) paymentDetails.get("id");
    String razorpayOrderId = (String) paymentDetails.get("order_id");

    // Get payment by razorpayOrderId
    Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
    if (payment == null) {
      log.warn("Payment not found for order ID: {}", razorpayOrderId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponseDTO<>(null, null));
    }

    Long orderId = payment.getOrder().getOrderId();
    log.info(
        "Processing webhook for payment ID: {}, order ID: {}, event: {}",
        razorpayPaymentId,
        orderId,
        event);

    // Handle the event based on type
    if ("payment.captured".equals(event)) {
      // Payment successful - mark as confirmed
      log.info("Processing payment.captured event for payment ID: {}", razorpayPaymentId);
      payment.setStatus(PaymentStatus.SUCCESS);
      payment.setRazorpayPaymentId(razorpayPaymentId);
      payment.setRazorpaySignature(signature);
      payment.setUpdatedAt(LocalDateTime.now());
      payment.setUpdatedBy(payment.getCreatedBy());

      Order order = payment.getOrder();
      order.setStatus(OrderStatus.CONFIRMED);
      order.setUpdatedAt(LocalDateTime.now());
      order.setUpdatedBy(order.getCreatedBy());

      // Save both entities
      paymentRepository.save(payment);
      orderRepository.save(order);

      log.info("Payment confirmed successfully for payment ID: {}", razorpayPaymentId);
      return ResponseEntity.ok().body(new BaseResponseDTO<>(null, null));
    } else if ("payment.failed".equals(event)) {
      // Payment failed - mark as failed and release reserved quantity
      log.info("Processing payment.failed event for payment ID: {}", razorpayPaymentId);
      payment.setStatus(PaymentStatus.FAILED);
      payment.setRazorpayPaymentId(razorpayPaymentId);
      payment.setRazorpaySignature(signature);
      payment.setUpdatedAt(LocalDateTime.now());
      payment.setUpdatedBy(payment.getCreatedBy());

      Order order = payment.getOrder();
      order.setStatus(OrderStatus.FAILED);
      order.setUpdatedAt(LocalDateTime.now());
      order.setUpdatedBy(order.getCreatedBy());

      // Release reserved quantity for all order items
      for (OrderItem item : order.getOrderItems()) {
        ProductVariant variant = item.getProductVariant();
        Integer currentReserved =
            variant.getReservedQuantity() != null ? variant.getReservedQuantity() : 0;
        variant.setReservedQuantity(currentReserved - item.getQuantity());
        productVariantRepository.save(variant);
      }

      // Save both entities
      paymentRepository.save(payment);
      orderRepository.save(order);

      log.info("Payment marked as failed for payment ID: {}", razorpayPaymentId);
      return ResponseEntity.ok().body(new BaseResponseDTO<>(null, null));
    } else {
      // Other events (like payment.authorized, etc.) we can ignore for now
      // but still return success to acknowledge receipt
      log.info("Ignoring webhook event: {} for payment ID: {}", event, razorpayPaymentId);
      return ResponseEntity.ok().body(new BaseResponseDTO<>(null, null));
    }
  }
}
