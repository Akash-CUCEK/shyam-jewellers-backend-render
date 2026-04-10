package com.shyam.dto.request;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
  private Long productId;
  private Integer quantity;
  private BigDecimal price;
}
