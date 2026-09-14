package com.shyam.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPurityByIdRequestDTO {

  @NotNull
  private Long purityId;
}