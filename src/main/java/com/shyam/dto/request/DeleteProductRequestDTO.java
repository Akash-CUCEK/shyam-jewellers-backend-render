package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteProductRequestDTO {
  @NotNull(message = "Product ID is required")
  private Long productId;

  @NotBlank(message = "updatedBy is required")
  private String updatedBy;
}
