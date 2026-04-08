package com.shyam.dto.request;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAllOrderRequestDTO {
  private LocalDate orderDate;
}
