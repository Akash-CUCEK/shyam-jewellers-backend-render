package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMaterialTypeRequestDTO {

  @NotBlank(message = "Name is required")
  private String name;

  private String createdBy;
}
