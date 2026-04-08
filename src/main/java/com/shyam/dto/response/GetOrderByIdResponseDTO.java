package com.shyam.dto.response;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetOrderByIdResponseDTO implements Serializable {
  private Long id;
  private String customerName;
  private String customerEmail;
  private String customerPhone;
  private String address;
  private List<Long> productIds;
  private LocalDate orderDate;
  private LocalTime orderTime;
  private String orderStatus;
  private String deliveryType;
  private Double totalCost;
  private Double dueAmount;
  private String paymentMethod;
  private String notes;
  private String createdById;
  private String createdByRole;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
