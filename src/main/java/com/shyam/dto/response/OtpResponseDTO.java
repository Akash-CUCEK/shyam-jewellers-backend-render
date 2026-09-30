package com.shyam.dto.response;

import com.shyam.common.dto.RefreshTokenBearer;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponseDTO implements RefreshTokenBearer {
  private String message;
  private String token;
  private String refreshToken;
}
