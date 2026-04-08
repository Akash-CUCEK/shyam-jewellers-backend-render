package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditPhotoRequestDTO {
  private String imgUrl;
  private Integer position;
}
