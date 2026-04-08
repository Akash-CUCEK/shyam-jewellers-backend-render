package com.shyam.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {
  private String refreshToken;
  private String email;
  private String role;
  private String deviceId;
}
