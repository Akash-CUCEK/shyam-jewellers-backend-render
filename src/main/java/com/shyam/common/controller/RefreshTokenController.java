package com.shyam.common.controller;

import com.shyam.common.dto.RefreshRequest;
import com.shyam.common.dto.RefreshTokenDetails;
import com.shyam.common.dto.RefreshTokenResponseDTO;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.exception.dto.ErrorMessagesDTO;
import com.shyam.common.exception.dto.ErrorResponseDTO;
import com.shyam.common.jwt.JwtUtil;
import com.shyam.common.service.Imp.TokenReuseDetectedException;
import com.shyam.common.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Refresh Token", description = "Refresh token endpoints")
public class RefreshTokenController {

  private final RefreshTokenService refreshTokenService;

  // keep this equal to RefreshTokenService.TOKEN_TTL
  private static final Duration COOKIE_MAX_AGE = Duration.ofHours(5);

  @Operation(summary = "Refresh token", description = "Refresh access token using refresh token.")
  @PostMapping("/refreshToken")
  public ResponseEntity<BaseResponseDTO<RefreshTokenResponseDTO>> refresh(
          @CookieValue(value = "refreshToken", required = false) String cookieToken,
          @RequestBody(required = false) RefreshRequest request,
          @RequestHeader(value = "X-Client-Type", defaultValue = "WEB", required = false)
          String clientType) {

    log.info("Received refresh token request");

    if (clientType == null || clientType.isBlank()) {
      clientType = "WEB";
    }

    // Determine the source of refresh token based on client type
    String refreshToken;
    if ("WEB".equalsIgnoreCase(clientType)) {
      refreshToken = cookieToken;
    } else {
      refreshToken = request != null ? request.getRefreshToken() : null;
    }

    if (refreshToken == null) {
      return ResponseEntity.status(401).body(buildError("Invalid refresh request"));
    }

    // *** Validate token. NOTE: email/role are no longer taken from the
    // client body - they come from whatever row the token hash matches, so
    // a client can no longer influence whose identity it gets back.
    RefreshTokenDetails details;
    try {
      details = refreshTokenService.validate(refreshToken);
    } catch (TokenReuseDetectedException e) {
      // Token was already rotated once before and is being replayed - every
      // session for this user has already been revoked inside validate().
      log.error(e.getMessage());
      return ResponseEntity.status(401).body(buildError("Invalid refresh token"));
    }

    if (details == null) {
      return ResponseEntity.status(401).body(buildError("Invalid or expired refresh token"));
    }

    // <<>> ROTATION - old token is marked revoked (not deleted) so a later
    // replay of this exact token is detectable as reuse next time
    refreshTokenService.markRevoked(refreshToken);

    String newAccessToken = JwtUtil.generateAccessToken(details.email(), details.role());
    String newRefreshToken = JwtUtil.generateRefreshToken();

    refreshTokenService.store(details.email(), details.role(), clientType, newRefreshToken);

    if ("WEB".equalsIgnoreCase(clientType)) {
      ResponseCookie cookie =
              ResponseCookie.from("refreshToken", newRefreshToken)
                      .httpOnly(true)
                      .secure(true)
                      .sameSite("None")
                      .path("/")
                      .maxAge(COOKIE_MAX_AGE)
                      .build();

      return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, cookie.toString())
              .body(
                      new BaseResponseDTO<>(
                              new RefreshTokenResponseDTO(newAccessToken, newRefreshToken), null));
    } else {
      return ResponseEntity.ok()
              .body(
                      new BaseResponseDTO<>(
                              new RefreshTokenResponseDTO(newAccessToken, newRefreshToken), null));
    }
  }

  private BaseResponseDTO<RefreshTokenResponseDTO> buildError(String message) {
    ErrorResponseDTO error =
            new ErrorResponseDTO(
                    List.of(new ErrorMessagesDTO(message)),
                    LocalDateTime.now(),
                    SYMErrorType.GENERIC_EXCEPTION,
                    "GENERIC_ERROR",
                    message);
    return new BaseResponseDTO<>(null, error);
  }
}