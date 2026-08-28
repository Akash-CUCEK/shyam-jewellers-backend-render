package com.shyam.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentRequestDTO {

  @NotNull private Long paymentId;

  @DecimalMin(value = "0.01", inclusive = true)
  private BigDecimal amount;

  private String reason;
  private String idempotencyKey;
}
