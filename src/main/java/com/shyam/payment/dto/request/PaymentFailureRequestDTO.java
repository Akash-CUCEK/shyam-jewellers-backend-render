package com.shyam.payment.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailureRequestDTO {

  private Long paymentId;
  private String paymentReference;
  private String gatewayOrderId;
  private String gatewayPaymentId;
  private String failureCode;
  private String failureReason;
  private String rawResponse;
}
