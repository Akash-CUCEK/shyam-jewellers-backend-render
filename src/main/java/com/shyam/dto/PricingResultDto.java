package com.shyam.dto;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingResultDto {

  private Long productId;

  private BigDecimal grossWeight;

  private BigDecimal netWeight;

  private BigDecimal metalRate;

  private BigDecimal purity;

  private BigDecimal purityFactor;

  private BigDecimal metalValue;

  private BigDecimal makingCharge;

  private BigDecimal finalPrice;
}
