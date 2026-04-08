package com.shyam.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderListPageResponseDTO {

    private List<OrderListResponseDTO> orders;

    private int currentPage;
    private int totalPages;
    private long totalElements;
}