package com.shyam.controller;

import com.shyam.entity.Order;
import com.shyam.entity.OrderCheckoutResponse;
import com.shyam.entity.Payment;
import com.shyam.entity.CartItem;
import com.shyam.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderCheckoutResponse> checkout(@RequestBody List<CartItem> cartItems,
                                                          @RequestParam Long addressId,
                                                          @RequestParam Long userId) {
        OrderCheckoutResponse response = orderService.createOrder(cartItems, addressId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/verify-payment")
    public ResponseEntity<Payment> verifyPayment(@PathVariable Long orderId,
                                                 @RequestParam String razorpayPaymentId,
                                                 @RequestParam String razorpayOrderId,
                                                 @RequestParam String razorpaySignature) {
        Payment payment = orderService.verifyPayment(orderId, razorpayPaymentId, razorpayOrderId, razorpaySignature);
        return ResponseEntity.ok(payment);
    }

    @GetMapping
    public ResponseEntity<Page<Order>> getOrders(@RequestParam Long userId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.getOrdersByUser(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }
}