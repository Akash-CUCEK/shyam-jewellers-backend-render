package com.shyam.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderListResponseDTO {
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
  private String paymentStatus;
  private String paymentMethod;
  private String notes;
  private String createdById;
  private String createdByRole;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
