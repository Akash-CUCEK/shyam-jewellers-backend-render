package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOfferPhotoResponseDTO implements Serializable {
  private String imgUrl;
  private Boolean isAvailable;
  private Integer position;
}
