package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.service.CookieService;
import com.shyam.dto.request.OtpRequestDTO;
import com.shyam.dto.request.logInRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "User", description = "User authentication and management endpoints")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;
  private final CookieService cookieService;

  @Operation(summary = "Login a user", description = "Login a User.")
  @PostMapping("/logIn")
  public BaseResponseDTO<LogInResponseDTO> login(@RequestBody logInRequestDTO logInRequestDTO) {

    log.info("Received request for sigIn");
    var response = userService.logIn(logInRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Verify OTP",
      description = "Verify the OTP sent to the user's email or phone.")
  @PostMapping("/verify")
  public ResponseEntity<BaseResponseDTO<OtpResponseDTO>> verify(
      @RequestBody OtpRequestDTO otpRequestDTO) {
    log.info("Received request for verify");

    ResponseEntity<OtpResponseDTO> responseEntity = userService.verify(otpRequestDTO);

    // Extract refresh token from response entity
    String refreshToken = responseEntity.getBody().getRefreshToken();

    ResponseCookie cookie = cookieService.createSecureCookie("refreshToken", refreshToken, (int) java.time.Duration.ofDays(1).getSeconds());

    return ResponseEntity.status(responseEntity.getStatusCode())
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new BaseResponseDTO<>(responseEntity.getBody(), null));
  }

  @Operation(summary = "Logout a user", description = "Logout a User.")
  @PostMapping("/logout")
  public ResponseEntity<BaseResponseDTO<LogoutResponseDTO>> logout(
      @RequestHeader("Authorization") String authorization,
      @CookieValue(value = "refreshToken", required = false) String refreshToken) {
    log.info("Received request for log out");
    String accessToken = authorization.replace("Bearer ", "");

    LogoutResponseDTO response = userService.logout(accessToken, refreshToken);

    ResponseCookie deleteCookie = cookieService.deleteCookie("refreshToken", "/");

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(new BaseResponseDTO<>(response, null));
  }
}
