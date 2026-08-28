package com.shyam.payment.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentRequestDTO {

  private String reason;
}
