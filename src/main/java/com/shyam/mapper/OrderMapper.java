//package com.shyam.mapper;
//
//import com.shyam.common.constants.OrderStatus;
//import com.shyam.common.constants.PaymentMethod;
//import com.shyam.common.constants.Role;
//import com.shyam.dao.AdminDAO;
//import com.shyam.dto.request.AddOrderRequestDTO;
//import com.shyam.dto.request.UpdateOrderRequestDTO;
//import com.shyam.dto.response.AddOrderResponseDTO;
//import com.shyam.dto.response.GetOrderByIdResponseDTO;
//import com.shyam.entity.AdminUsers;
//import com.shyam.entity.Order;
//import com.shyam.entity.OrderItem;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//
//import static com.shyam.common.util.MapperUtil.*;
//
//@Slf4j
//@Component
//@AllArgsConstructor
//public class OrderMapper {
//    private final AdminDAO adminDAO;
//
//    public Order mapToOrderEntity(AddOrderRequestDTO dto) {
//        log.info("Mapping to order entity");
//
//        Order order = Order.builder()
//                .customerName(dto.getCustomerName())
//                .customerEmail(dto.getCustomerEmail())
//                .customerPhone(dto.getCustomerPhone())
//                .address(dto.getAddress())
//                .orderDate(LocalDate.now())
//                .orderTime(LocalTime.now())
//                .orderStatus(parseOrderStatus(dto.getOrderStatus()))
//                .deliveryType(dto.getDeliveryType())
//                .paymentMethod(parsePaymentMethod(dto.getPaymentMethod()))
//                .totalCost(dto.getTotalCost())
//                .dueAmount(dto.getDueAmount())
//                .notes(dto.getNotes())
//                .createdById(parseCreatedById(dto.getCreatedBy()))
//                .createdByRole(parseRole(dto.getCreatedByRole()))
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        List<OrderItem> items = dto.getProducts().stream().map(p -> {
//            OrderItem item = new OrderItem();
//            item.setOrder(order);
//            item.setProductId(p.getProductId());
//            item.setQuantity(p.getQuantity());
//            item.setPrice(p.getPrice());
//            return item;
//        }).toList();
//
//        order.setItems(items);
//
//        return order;
//    }
//    private OrderStatus parseOrderStatus(String status) {
//        if (status == null) return OrderStatus.PENDING;
//        return OrderStatus.valueOf(status.toUpperCase());
//    }
//
//    private PaymentMethod parsePaymentMethod(String method) {
//        if (method == null) return PaymentMethod.CASH;
//        return PaymentMethod.valueOf(method.toUpperCase());
//    }
//
//    private Role parseRole(String role) {
//        if (role == null) return Role.ADMIN;
//        return Role.valueOf(role.toUpperCase());
//    }
//
//    private Long parseCreatedById(String email) {
//        if (email == null) {
//            throw new IllegalArgumentException("CreatedBy email is null");
//        }
//        AdminUsers admin = adminDAO.findUserByEmail(email);
//        return admin.getId();
//    }
//
//    public GetOrderByIdResponseDTO mapToOrderResponseDTO(Order order) {
//        return GetOrderByIdResponseDTO.builder()
//                .id(order.getId())
//                .customerName(order.getCustomerName())
//                .customerEmail(order.getCustomerEmail())
//                .customerPhone(order.getCustomerPhone())
//                .address(order.getAddress())
//                .productIds(order.getProductIds())
//                .orderDate(order.getOrderDate())
//                .orderTime(order.getOrderTime())
//                .orderStatus(
//                        order.getOrderStatus() != null ? order.getOrderStatus().name() : null
//                )
//                .deliveryType(order.getDeliveryType())
//                .totalCost(
//                        order.getTotalCost() != null ? order.getTotalCost().doubleValue() : 0.0
//                )
//                .dueAmount(
//                        order.getDueAmount() != null ? order.getDueAmount().doubleValue() : 0.0
//                )
//                .paymentMethod(
//                        order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null
//                )
//                .notes(order.getNotes())
//                .createdById(order.getCreatedById())
//                .createdByRole(
//                        order.getCreatedByRole() != null ? order.getCreatedByRole().name() : null
//                )
//                .createdAt(order.getCreatedAt())
//                .updatedAt(order.getUpdatedAt())
//                .build();
//    }
//
//    /* ===================== MESSAGE RESPONSE ===================== */
//
//    public AddOrderResponseDTO mapToOrderCreateInMessage(String message) {
//        return AddOrderResponseDTO.builder()
//                .message(message)
//                .build();
//    }
//    public void updateOrderEntity(Order order, UpdateOrderRequestDTO dto) {
//
//        if (dto.getCustomerName() != null)
//            order.setCustomerName(dto.getCustomerName());
//
//        if (dto.getCustomerEmail() != null)
//            order.setCustomerEmail(dto.getCustomerEmail());
//
//        if (dto.getCustomerPhone() != null)
//            order.setCustomerPhone(dto.getCustomerPhone());
//
//        if (dto.getAddress() != null)
//            order.setAddress(dto.getAddress());
//
//        if (dto.getProducts() != null)
//            order.setProductIds(dto.getProducts());
//
//        if (dto.getOrderStatus() != null)
//            order.setOrderStatus(parseOrderStatus(dto.getOrderStatus()));
//
//        if (dto.getDeliveryType() != null)
//            order.setDeliveryType(dto.getDeliveryType());
//
//        if (dto.getTotalCost() != null)
//            order.setTotalCost(dto.getTotalCost());
//
//        if (dto.getDueAmount() != null)
//            order.setDueAmount(dto.getDueAmount());
//
//        if (dto.getPaymentMethod() != null)
//            order.setPaymentMethod(parsePaymentMethod(dto.getPaymentMethod()));
//
//        if (dto.getNotes() != null)
//            order.setNotes(dto.getNotes());
//
//        order.setUpdatedAt(LocalDateTime.now());
//    }
//
//}
