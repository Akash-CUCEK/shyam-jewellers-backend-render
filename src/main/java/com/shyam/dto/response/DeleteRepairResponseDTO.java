package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRepairResponseDTO implements Serializable {
  private String response;
}
