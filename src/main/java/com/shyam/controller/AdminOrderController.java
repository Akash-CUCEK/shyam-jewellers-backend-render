//package com.shyam.controller;
//
//import com.shyam.entity.Order;
//import com.shyam.service.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/admin/orders")
//public class AdminOrderController {
//
//    private final OrderService orderService;
//
//    @Autowired
//    public AdminOrderController(OrderService orderService) {
//        this.orderService = orderService;
//    }
//
//    @GetMapping
//    public ResponseEntity<Page<Order>> getAllOrders(@RequestParam(defaultValue = "0") int page,
//                                                    @RequestParam(defaultValue = "10") int size,
//                                                    @RequestParam(required = false) String status,
//                                                    @RequestParam(required = false) String startDate,
//                                                    @RequestParam(required = false) String endDate) {
//        // TODO: Implement filtering by status and date
//        // For now, we'll ignore the filters and return all orders
//        Page<Order> orders = orderService.getAllOrders(page, size, status, startDate, endDate);
//        return ResponseEntity.ok(orders);
//    }
//
//    @GetMapping("/{orderId}")
//    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
//        Order order = orderService.getOrderById(orderId);
//        if (order == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(order);
//    }
//
//    @PatchMapping("/{orderId}/status")
//    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId,
//                                                   @RequestParam String status) {
//        Order order = orderService.updateOrderStatus(orderId, status);
//        return ResponseEntity.ok(order);
//    }
//}