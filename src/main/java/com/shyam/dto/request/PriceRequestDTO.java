package com.shyam.dto.request;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRequestDTO {
  private BigDecimal price;
}
