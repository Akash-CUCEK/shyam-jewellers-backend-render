package com.shyam.dto.response;

import com.shyam.common.dto.RefreshTokenBearer;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyAdminResponseDTO implements RefreshTokenBearer {
  private String token;
  private String refreshToken;
  private String message;
}
