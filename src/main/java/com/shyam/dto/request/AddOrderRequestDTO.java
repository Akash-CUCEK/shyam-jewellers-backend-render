package com.shyam.dto.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderRequestDTO {

  private String customerName;
  private String customerEmail;
  private String customerPhone;
  private String address;

  private List<ProductDTO> products;

  private String orderStatus;
  private String deliveryType;
  private String paymentMethod;

  private BigDecimal totalCost;
  private BigDecimal dueAmount;

  private String notes;
  private String createdBy;
  private String createdByRole;
}
