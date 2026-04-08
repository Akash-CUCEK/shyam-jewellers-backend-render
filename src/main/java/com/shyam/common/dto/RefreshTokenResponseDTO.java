package com.shyam.common.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenResponseDTO implements Serializable {
  private String accessToken;
  private String refreshToken;
}
