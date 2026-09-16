package com.shyam.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPurityRequestDTO {
  @NotNull(message = "Material type is required")
  private Long materialTypeId;

  @NotBlank(message = "Purity name is required")
  private String purityName;

  @NotNull(message = "Purity factor is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Purity factor must be positive")
  private BigDecimal purityFactor;

  @NotBlank private String createdBy;
}
