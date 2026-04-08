package com.shyam.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

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