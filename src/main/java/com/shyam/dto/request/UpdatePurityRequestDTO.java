package com.shyam.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePurityRequestDTO {
  @NotNull(message = "Purity ID is required")
  private Long purityId;

  @NotNull(message = "Material type is required")
  private Long materialTypeId;

  @NotBlank(message = "Purity name is required")
  private String purityName;

  @NotNull(message = "Purity factor is required")
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal purityFactor;

  @NotBlank private String updatedBy;
}
