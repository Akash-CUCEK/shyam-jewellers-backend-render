package com.shyam.controller;

import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.entity.Order;
import com.shyam.entity.OrderItem;
import com.shyam.entity.Payment;
import com.shyam.entity.ProductVariant;
import com.shyam.common.constants.OrderStatus;
import com.shyam.common.constants.PaymentStatus;
import com.shyam.config.RazorpayConfig;
import com.shyam.repository.OrderRepository;
import com.shyam.repository.PaymentRepository;
import com.shyam.repository.ProductVariantRepository;
import com.shyam.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller for handling Razorpay webhooks.
 */
@RestController
@RequestMapping("/webhooks")
@Tag(name = "Webhook", description = "Endpoints for handling Razorpay webhooks")
public class WebhookController {

    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final RazorpayConfig razorpayConfig;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;

    @Autowired
    public WebhookController(OrderService orderService, PaymentRepository paymentRepository, RazorpayConfig razorpayConfig,
                             OrderRepository orderRepository, ProductVariantRepository productVariantRepository) {
        this.orderService = orderService;
        this.paymentRepository = paymentRepository;
        this.razorpayConfig = razorpayConfig;
        this.orderRepository = orderRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<?> handleRazorpayWebhook(@RequestBody Map<String, Object> payload,
                                                   @RequestHeader("X-Razorpay-Signature") String signature) {
        // Verify the signature using Razorpay secret
        boolean isValidSignature = false;
        try {
            // For webhook verification, we need the raw payload
            // Since we only have the parsed Map, we'll reconstruct it
            // Note: In production, you should configure Spring to expose the raw request body
            if (payload == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payload is null");
            }
            String payloadString = new JSONObject(payload).toString();
            isValidSignature = Utils.verifySignature(payloadString, signature, razorpayConfig.getKeySecret());
        } catch (Exception e) {
            // If there's any error in verification, treat as invalid signature
            isValidSignature = false;
        }

        if (!isValidSignature) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Parse the payload safely with null checks
        String event = (String) payload.get("event");
        if (event == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing event in webhook payload");
        }

        Map<String, Object> payloadData = (Map<String, Object>) payload.get("payload");
        if (payloadData == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing payload in webhook");
        }

        Map<String, Object> paymentEntity = (Map<String, Object>) payloadData.get("payment");
        if (paymentEntity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing payment in webhook payload");
        }

        Map<String, Object> paymentDetails = (Map<String, Object>) paymentEntity.get("entity");
        if (paymentDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing payment entity in webhook payload");
        }

        String razorpayPaymentId = (String) paymentDetails.get("id");
        String razorpayOrderId = (String) paymentDetails.get("order_id");

        // Get payment by razorpayOrderId
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (payment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found for order ID: " + razorpayOrderId);
        }

        Long orderId = payment.getOrder().getOrderId();

        // Handle the event based on type
        if ("payment.captured".equals(event)) {
            // Payment successful - mark as confirmed
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

            return ResponseEntity.ok().build();
        } else if ("payment.failed".equals(event)) {
            // Payment failed - mark as failed and release reserved quantity
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
                Integer currentReserved = variant.getReservedQuantity() != null ? variant.getReservedQuantity() : 0;
                variant.setReservedQuantity(currentReserved - item.getQuantity());
                productVariantRepository.save(variant);
            }

            // Save both entities
            paymentRepository.save(payment);
            orderRepository.save(order);

            return ResponseEntity.ok().build();
        } else {
            // Other events (like payment.authorized, etc.) we can ignore for now
            // but still return success to acknowledge receipt
            return ResponseEntity.ok().build();
        }
    }
}