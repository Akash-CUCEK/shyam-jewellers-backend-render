package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOfferPhotoResponseDTO implements Serializable {
  private String imgUrl1;
  private String imgUrl2;
  private String imgUrl3;
  private String imgUrl4;
  private String imgUrl5;
}
