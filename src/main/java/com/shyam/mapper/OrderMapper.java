package com.shyam.mapper;

import com.shyam.common.constants.*;
import com.shyam.dto.request.AddOrderRequestDTO;
import com.shyam.dto.request.UpdateOrderRequestDTO;
import com.shyam.dto.response.AddOrderResponseDTO;
import com.shyam.dto.response.GetOrderByIdResponseDTO;
import com.shyam.dto.response.OrderListResponseDTO;
import com.shyam.entity.Order;
import com.shyam.entity.OrderItem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@Component
@Slf4j
public class OrderMapper {

  // ================= CREATE =================

  public Order mapToOrderEntity(AddOrderRequestDTO dto) {

    if (dto.getProducts() == null || dto.getProducts().isEmpty()) {
      throw new RuntimeException("Products cannot be empty");
    }

    Order order = Order.builder()
            .customerName(dto.getCustomerName())
            .customerEmail(dto.getCustomerEmail())
            .customerPhone(dto.getCustomerPhone())
            .address(dto.getAddress())

            .orderDate(LocalDate.now())
            .orderTime(LocalTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())

            .orderStatus(parseOrderStatus(dto.getOrderStatus()))
            .paymentMethod(parsePaymentMethod(dto.getPaymentMethod()))
            .createdByRole(parseRole(dto.getCreatedByRole()))

            .deliveryType(dto.getDeliveryType())

            .totalCost(dto.getTotalCost())
            .dueAmount(dto.getDueAmount())

            .paymentStatus(calculatePaymentStatus(dto.getTotalCost(), dto.getDueAmount()))

            .notes(dto.getNotes())
            .createdBy(dto.getCreatedBy())
            .build();

    List<OrderItem> items = dto.getProducts().stream().map(p -> {
      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProductId(p.getProductId());
      item.setQuantity(p.getQuantity());
      item.setPrice(p.getPrice());
      return item;
    }).toList();

    order.setItems(items);

    return order;
  }

  // ================= UPDATE =================

  public void updateOrderEntity(Order order, UpdateOrderRequestDTO dto) {

    if (dto.getCustomerName() != null)
      order.setCustomerName(dto.getCustomerName());

    if (dto.getCustomerEmail() != null)
      order.setCustomerEmail(dto.getCustomerEmail());

    if (dto.getCustomerPhone() != null)
      order.setCustomerPhone(dto.getCustomerPhone());

    if (dto.getAddress() != null)
      order.setAddress(dto.getAddress());

    if (dto.getDeliveryType() != null)
      order.setDeliveryType(dto.getDeliveryType());

    if (dto.getNotes() != null)
      order.setNotes(dto.getNotes());

    if (dto.getOrderStatus() != null)
      order.setOrderStatus(parseOrderStatus(dto.getOrderStatus()));

    if (dto.getPaymentMethod() != null)
      order.setPaymentMethod(parsePaymentMethod(dto.getPaymentMethod()));

    if (dto.getTotalCost() != null)
      order.setTotalCost(dto.getTotalCost());

    if (dto.getDueAmount() != null)
      order.setDueAmount(dto.getDueAmount());

    order.setPaymentStatus(
            calculatePaymentStatus(order.getTotalCost(), order.getDueAmount())
    );

    // 🔥 replace items
    if (dto.getProducts() != null && !dto.getProducts().isEmpty()) {

      order.getItems().clear();

      List<OrderItem> items = dto.getProducts().stream().map(productId -> {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(productId);
        item.setQuantity(1);
        item.setPrice(BigDecimal.ZERO);
        return item;
      }).toList();

      order.getItems().addAll(items);
    }

    order.setUpdatedAt(LocalDateTime.now());
  }

  // ================= RESPONSE =================

  public GetOrderByIdResponseDTO mapToOrderResponseDTO(Order order) {

    return GetOrderByIdResponseDTO.builder()
            .id(order.getId())
            .customerName(order.getCustomerName())
            .customerEmail(order.getCustomerEmail())
            .customerPhone(order.getCustomerPhone())
            .address(order.getAddress())

            .productIds(
                    order.getItems()
                            .stream()
                            .map(OrderItem::getProductId)
                            .toList()
            )

            .orderDate(order.getOrderDate())
            .orderTime(order.getOrderTime())

            .orderStatus(order.getOrderStatus().name())
            .deliveryType(order.getDeliveryType())

            .totalCost(order.getTotalCost().doubleValue())
            .dueAmount(order.getDueAmount().doubleValue())

            .paymentMethod(order.getPaymentMethod().name())

            .notes(order.getNotes())

            .createdById(order.getCreatedBy())
            .createdByRole(order.getCreatedByRole().name())

            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())

            .build();
  }

  public AddOrderResponseDTO mapToOrderCreateInMessage(String message) {
    return AddOrderResponseDTO.builder().message(message).build();
  }

  // ================= ENUM =================

  private OrderStatus parseOrderStatus(String status) {
    try {
      return OrderStatus.valueOf(status.toUpperCase());
    } catch (Exception e) {
      return OrderStatus.CREATED;
    }
  }

  private PaymentMethod parsePaymentMethod(String method) {
    try {
      return PaymentMethod.valueOf(method.toUpperCase());
    } catch (Exception e) {
      return PaymentMethod.CASH;
    }
  }

  private Role parseRole(String role) {
    try {
      return Role.valueOf(role.toUpperCase());
    } catch (Exception e) {
      return Role.ADMIN;
    }
  }

  private OrderPaymentStatus calculatePaymentStatus(BigDecimal total, BigDecimal due) {

    if (due == null || due.compareTo(BigDecimal.ZERO) == 0)
      return OrderPaymentStatus.PAID;

    if (due.compareTo(total) < 0)
      return OrderPaymentStatus.PARTIALLY_PAID;

    return OrderPaymentStatus.UNPAID;
  }

  public OrderListResponseDTO mapToOrderListDTO(Order order) {

    return OrderListResponseDTO.builder()
            .orderId(order.getId())
            .customerName(order.getCustomerName())

            .orderDate(order.getOrderDate())
            .orderTime(order.getOrderTime())

            .orderStatus(order.getOrderStatus().name())

            .totalCost(order.getTotalCost().doubleValue())

            // 🔥 total items count
            .totalItems(
                    order.getItems() != null ? order.getItems().size() : 0
            )

            .build();
  }
}