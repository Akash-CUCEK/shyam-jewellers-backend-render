package com.shyam.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMaterialTypeByIdRequestDTO {
  @NotNull(message = "Id is required")
  private Long materialTypeId;
}
