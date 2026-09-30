package com.shyam.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {
  // Only needed for MOBILE clients - WEB reads the token from its httpOnly
  // cookie instead. email/role are gone: identity now comes from whichever
  // DB row the token's hash matches (see RefreshTokenService.validate),
  // so a client can no longer spoof who it's refreshing as.
  private String refreshToken;
}