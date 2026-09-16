package com.shyam.dto;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceBreakdownDTO {

    private BigDecimal metalValue;
    private BigDecimal makingCharge;
    private BigDecimal gst;
    private BigDecimal finalPrice;
    private BigDecimal ratePerGramUsed;
    private BigDecimal purityFactorUsed;
}