package com.shyam.dto.request;

import com.shyam.entity.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePurityRequestDTO {

  @NotNull
  private Long purityId;

  @NotNull
  private MaterialType materialType;

  @NotBlank(message = "Purity name is required")
  private String purityName;

  @NotNull
  private BigDecimal purityFactor;

  private String updatedBy;
}