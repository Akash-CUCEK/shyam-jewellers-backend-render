package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMaterialTypeRequestDTO {

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Making charge type is required")
  private String makingChargeType;

  @NotNull
  @Positive(message = "Making charge value must be positive")
  private BigDecimal makingChargeValue;

  private String createdBy;
}
