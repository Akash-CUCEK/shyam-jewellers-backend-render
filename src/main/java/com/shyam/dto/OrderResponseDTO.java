package com.shyam.dto;

import com.shyam.common.constants.OrderStatus;
import com.shyam.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

  private Long orderId;

  private String orderNumber;

  private OrderStatus status;

  private BigDecimal totalAmount;

  private LocalDateTime createdAt;

  private List<OrderItemResponseDTO> items;

  // Added because OrderController needs these
  private Long userId;

  private String createdBy;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class OrderItemResponseDTO {

    private Long orderItemId;

    private String skuCodeSnapshot;

    private String productNameSnapshot;

    private BigDecimal weightSnapshot;

    private BigDecimal metalValueSnapshot;

    private BigDecimal makingChargeSnapshot;

    private BigDecimal gstSnapshot;

    private BigDecimal finalPriceSnapshot;

    private Integer quantity;
  }

  public static OrderResponseDTO fromEntity(Order order) {

    if (order == null) {
      return null;
    }

    List<OrderItemResponseDTO> itemDTOs = new ArrayList<>();

    if (order.getOrderItems() != null) {

      itemDTOs =
          order.getOrderItems().stream()
              .map(
                  item ->
                      OrderItemResponseDTO.builder()
                          .orderItemId(item.getOrderItemId())
                          .skuCodeSnapshot(item.getSkuCodeSnapshot())
                          .productNameSnapshot(item.getProductNameSnapshot())
                          .weightSnapshot(item.getWeightSnapshot())
                          .metalValueSnapshot(item.getMetalValueSnapshot())
                          .makingChargeSnapshot(item.getMakingChargeSnapshot())
                          .gstSnapshot(item.getGstSnapshot())
                          .finalPriceSnapshot(item.getFinalPriceSnapshot())
                          .quantity(item.getQuantity())
                          .build())
              .collect(Collectors.toList());
    }

    return OrderResponseDTO.builder()
        .orderId(order.getOrderId())
        .orderNumber(order.getOrderNumber())
        .status(order.getStatus())
        .totalAmount(order.getTotalAmount())
        .createdAt(order.getCreatedAt())

        // Added for controller ownership check
        .userId(order.getUser() != null ? order.getUser().getUserId() : null)

        // Added for cancelOrder()
        .createdBy(order.getCreatedBy())
        .items(itemDTOs)
        .build();
  }
}
