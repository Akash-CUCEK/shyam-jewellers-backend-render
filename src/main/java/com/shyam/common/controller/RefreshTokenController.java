package com.shyam.common.controller;

import com.shyam.common.dto.RefreshRequest;
import com.shyam.common.dto.RefreshTokenResponseDTO;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.exception.dto.ErrorMessagesDTO;
import com.shyam.common.exception.dto.ErrorResponseDTO;
import com.shyam.common.jwt.JwtUtil;
import com.shyam.common.service.RefreshTokenService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {

  private final RefreshTokenService refreshTokenService;

  @PostMapping("/refreshToken")
  public ResponseEntity<BaseResponseDTO<RefreshTokenResponseDTO>> refresh(
      @CookieValue(value = "refreshToken", required = false) String cookieToken,
      @RequestBody(required = false) RefreshRequest request) {

    log.info("Received refresh token request");

    // 🔥 Extract data (web + mobile)
    String refreshToken = request != null ? request.getRefreshToken() : cookieToken;
    String email = request != null ? request.getEmail() : null;
    String role = request != null ? request.getRole() : null;
    String deviceId = request != null ? request.getDeviceId() : null;

    // ❌ Invalid request
    if (refreshToken == null || email == null || role == null || deviceId == null) {
      return ResponseEntity.status(401).body(buildError("Invalid refresh request"));
    }

    // ✅ Validate token
    var details = refreshTokenService.validate(refreshToken, email, role, deviceId);

    if (details == null) {
      return ResponseEntity.status(401).body(buildError("Invalid refresh token"));
    }

    // 🔁 ROTATION
    refreshTokenService.delete(email, role, deviceId);

    String newAccessToken = JwtUtil.generateAccessToken(email, role);
    String newRefreshToken = JwtUtil.generateRefreshToken();

    refreshTokenService.store(email, role, newRefreshToken, deviceId);

    // 🌐 Cookie for web
    ResponseCookie cookie =
        ResponseCookie.from("refreshToken", newRefreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(Duration.ofDays(1))
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(
            new BaseResponseDTO<>(
                new RefreshTokenResponseDTO(newAccessToken, newRefreshToken), null));
  }

  // 🔥 Helper method (OUTSIDE main method)
  private BaseResponseDTO<RefreshTokenResponseDTO> buildError(String message) {
    ErrorResponseDTO error =
        new ErrorResponseDTO(
            List.of(new ErrorMessagesDTO(message)),
            LocalDateTime.now(),
            SYMErrorType.GENERIC_EXCEPTION);
    return new BaseResponseDTO<>(null, error);
  }
}
