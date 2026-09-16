// GetPurityByIdRequestDTO.java
package com.shyam.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPurityByIdRequestDTO {
  @NotNull(message = "Purity ID is required")
  private Long purityId;
}
