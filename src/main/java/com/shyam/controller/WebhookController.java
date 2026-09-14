package com.shyam.controller;

import com.shyam.entity.Payment;
import com.shyam.repository.PaymentRepository;
import com.shyam.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final OrderService orderService;
    private final PaymentRepository paymentRepository;

    @Autowired
    public WebhookController(OrderService orderService, PaymentRepository paymentRepository) {
        this.orderService = orderService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<?> handleRazorpayWebhook(@RequestBody Map<String, Object> payload,
                                                   @RequestHeader("X-Razorpay-Signature") String signature) {
        // TODO: Verify the signature using Razorpay secret
        // For now, we'll assume the signature is valid.
        boolean isValidSignature = true; // Placeholder

        if (!isValidSignature) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Parse the payload
        String event = (String) payload.get("event");
        Map<String, Object> payloadData = (Map<String, Object>) payload.get("payload");
        Map<String, Object> paymentEntity = (Map<String, Object>) payloadData.get("payment");
        Map<String, Object> paymentDetails = (Map<String, Object>) paymentEntity.get("entity");

        String razorpayPaymentId = (String) paymentDetails.get("id");
        String razorpayOrderId = (String) paymentDetails.get("order_id");
        String status = (String) paymentDetails.get("status"); // e.g., captured, failed, etc.

        // Get payment by razorpayOrderId
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (payment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found for order ID: " + razorpayOrderId);
        }

        Long orderId = payment.getOrder().getOrderId();

        // Update payment and order status based on the event
        if ("payment.captured".equals(event)) {
            // Payment successful
            Payment updatedPayment = orderService.verifyPayment(orderId, razorpayPaymentId, razorpayOrderId, signature);
            return ResponseEntity.ok().build();
        } else if ("payment.failed".equals(event)) {
            // Payment failed
            // We'll mark the payment as failed and order as failed
            // We'll need to update the payment entity and order entity.
            // We'll set the payment status to FAILED and order status to FAILED
            // and release the reserved quantity.
            // We'll do this in a separate method in orderService.
            // For now, we'll leave it as a TODO.
            return ResponseEntity.ok().build();
        } else {
            // Other events, we can ignore
            return ResponseEntity.ok().build();
        }
    }
}