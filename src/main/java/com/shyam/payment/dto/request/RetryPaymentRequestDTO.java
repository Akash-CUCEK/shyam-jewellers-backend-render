package com.shyam.payment.dto.request;

import com.shyam.common.constants.PaymentGateway;
import com.shyam.common.constants.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryPaymentRequestDTO {

  private PaymentMethod paymentMethod;
  private PaymentGateway gateway;

  @DecimalMin(value = "0.01", inclusive = true)
  private BigDecimal amount;

  private String idempotencyKey;
}
