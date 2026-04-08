package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderInvoiceResponse implements Serializable {
  private byte[] invoicePdfBytes;
  private String fileName;
}
