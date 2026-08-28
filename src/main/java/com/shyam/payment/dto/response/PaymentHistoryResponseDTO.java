package com.shyam.payment.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponseDTO {

  private Long orderId;
  private List<PaymentResponseDTO> payments;
}
