package com.shyam.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderListPageResponseDTO {

  private List<OrderListResponseDTO> orders;

  private int currentPage;
  private int totalPages;
  private long totalElements;
}
