package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMaterialTypeRequestDTO {
  @NotNull(message = "Id is required")
  private Long materialTypeId;
  @NotBlank(message = "Name is required")
  private String name;
  private String updatedBy;
}
