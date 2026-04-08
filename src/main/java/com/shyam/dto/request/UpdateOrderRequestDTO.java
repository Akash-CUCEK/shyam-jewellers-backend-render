package com.shyam.dto.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderRequestDTO {

  private Long orderId;

  private String customerName;
  private String customerEmail;
  private String customerPhone;
  private String address;

  private List<Long> products;

  private String orderStatus;
  private String deliveryType;

  private BigDecimal totalCost;
  private BigDecimal dueAmount;

  private String paymentMethod;
  private String notes;
}
