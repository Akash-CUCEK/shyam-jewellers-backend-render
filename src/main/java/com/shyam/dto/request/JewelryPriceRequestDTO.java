package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JewelryPriceRequestDTO {

  private Long materialTypeId;
  private java.math.BigDecimal netWeight;
  private java.math.BigDecimal purity;
}
