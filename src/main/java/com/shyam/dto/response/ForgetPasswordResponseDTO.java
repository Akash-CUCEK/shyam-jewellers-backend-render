package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgetPasswordResponseDTO implements Serializable {
  private String response;
}
