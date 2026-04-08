package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponseDTO implements Serializable {
  private String message;
  private String token;
}
