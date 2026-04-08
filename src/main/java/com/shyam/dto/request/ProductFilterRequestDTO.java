package com.shyam.dto.request;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterRequestDTO {
  private String category;
  private String materialType;
  private String gender;
  private Boolean isAvailable;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private BigDecimal minWeight;
  private BigDecimal maxWeight;
  private Integer minAvailableStock;
  private Integer maxAvailableStock;
}
