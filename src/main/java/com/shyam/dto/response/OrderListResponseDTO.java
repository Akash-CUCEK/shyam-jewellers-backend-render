package com.shyam.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderListResponseDTO {

  private Long orderId;
  private String customerName;

  private LocalDate orderDate;
  private LocalTime orderTime;

  private String orderStatus;

  private Double totalCost;

  private Integer totalItems;
}
