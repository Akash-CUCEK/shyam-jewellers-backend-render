package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditPhotoResponseDTO implements Serializable {
  private String response;
}
