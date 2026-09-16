package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetProductByIdRequestDTO {

  @NotBlank(message = "Product ID is required")
  private Long productId;
}
