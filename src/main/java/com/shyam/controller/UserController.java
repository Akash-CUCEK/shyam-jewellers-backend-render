package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.service.CookieService;
import com.shyam.common.util.AuthResponseHelper;
import com.shyam.dto.request.LogoutRequestDTO;
import com.shyam.dto.request.OtpRequestDTO;
import com.shyam.dto.request.logInRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

  @PostMapping("/verify")
  public ResponseEntity<BaseResponseDTO<OtpResponseDTO>> verify(
      @RequestBody OtpRequestDTO otpRequestDTO,
      @RequestHeader(value = "X-Client-Type", defaultValue = "WEB", required = false)
          String clientType) {
    log.info("Received request for verify");

    ResponseEntity<OtpResponseDTO> responseEntity = userService.verify(otpRequestDTO);

    ResponseEntity<BaseResponseDTO<OtpResponseDTO>> wrapped =
        ResponseEntity.status(responseEntity.getStatusCode())
            .body(new BaseResponseDTO<>(responseEntity.getBody(), null));

    return AuthResponseHelper.handleResponse(clientType, wrapped, cookieService);
  }

  @PostMapping("/logout")
  public ResponseEntity<BaseResponseDTO<LogoutResponseDTO>> logout(
      @RequestHeader("Authorization") String authorization,
      @CookieValue(value = "refreshToken", required = false) String refreshTokenFromCookie,
      @RequestBody(required = false) LogoutRequestDTO logoutRequestDTO) {

    String accessToken = authorization.replace("Bearer ", "");

    // WEB se cookie milega, MOBILE se body me aayega
    String refreshToken =
        refreshTokenFromCookie != null
            ? refreshTokenFromCookie
            : (logoutRequestDTO != null ? logoutRequestDTO.getRefreshToken() : null);

    LogoutResponseDTO response = userService.logout(accessToken, refreshToken);

    ResponseCookie deleteCookie = cookieService.deleteCookie("refreshToken", "/");

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(new BaseResponseDTO<>(response, null));
  }
}
