package com.shyam.dto.request;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAddRequestDTO {
  private String email;
  private String category;
  private BigDecimal price;
  private Integer discountPercentage;
  private BigDecimal weight;
  private String materialType;
  private String gender;
  private String shortDescription;
  private String fullDescription;
  private Boolean isAvailable;
  private Integer quantity;
}
