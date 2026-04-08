package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderInvoiceRequest {
  private Long orderId;
}
