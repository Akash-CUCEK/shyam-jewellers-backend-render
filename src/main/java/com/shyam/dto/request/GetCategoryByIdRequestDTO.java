package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCategoryByIdRequestDTO {
  private Long id;
}
