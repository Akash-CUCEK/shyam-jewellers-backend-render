package com.shyam.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentRequestDTO {

  private Long paymentId;
  private String paymentReference;
  private String gatewayOrderId;
  private String gatewayPaymentId;
  private String signature;
  private String status;

  @DecimalMin(value = "0.01", inclusive = true)
  private BigDecimal amount;

  private String rawResponse;
}
