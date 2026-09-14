package com.shyam.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetProductByIdRequestDTO {

  @NotNull
  private Long productId;
}