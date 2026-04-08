package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyAdminResponseDTO implements Serializable {
  private String token;
  private String refreshToken;
  private String message;
}
