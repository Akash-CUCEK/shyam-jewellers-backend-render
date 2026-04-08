package com.shyam.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCategoryUserResponseDTO {
  private Long categoryId;
  private String name;
  private String imageUrl;
  private Boolean showOnHome;
}
