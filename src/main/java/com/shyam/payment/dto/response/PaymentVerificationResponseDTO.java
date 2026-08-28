package com.shyam.payment.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResponseDTO {

  private PaymentResponseDTO payment;
  private String orderStatus;
  private String orderPaymentStatus;
  private boolean invoiceGenerated;
  private String invoiceFileName;
  private byte[] invoicePdfBytes;
}
