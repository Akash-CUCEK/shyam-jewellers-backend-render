package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.OtpRequestDTO;
import com.shyam.dto.request.logInRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  @Operation(summary = "Login a user", description = "Login a User.")
  @PostMapping("/logIn")
  public BaseResponseDTO<LogInResponseDTO> register(@RequestBody logInRequestDTO logInRequestDTO) {

    logger.info("Received request for sigIn");
    var response = userService.logIn(logInRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @PostMapping("/verify")
  public ResponseEntity<BaseResponseDTO<OtpResponseDTO>> verify(
      @RequestBody OtpRequestDTO otpRequestDTO) {
    logger.info("Received request for verify");

    ResponseEntity<OtpResponseDTO> responseEntity = userService.verify(otpRequestDTO);

    return ResponseEntity.status(responseEntity.getStatusCode())
        .headers(responseEntity.getHeaders())
        .body(new BaseResponseDTO<>(responseEntity.getBody(), null));
  }

  //  @Operation(summary = "Logout a user", description = "Logout a User.")
  //  @PostMapping("/logout")
  //  public ResponseEntity<BaseResponseDTO<LogoutResponseDTO>> logout(
  //      @RequestHeader("Authorization") String authorization,
  //      @CookieValue(value = "refreshToken", required = false) String refreshToken) {
  //    logger.info("Received request for log out ");
  //    String accessToken = authorization.replace("Bearer ", "");
  //
  //    LogoutResponseDTO response = userService.logout(accessToken, refreshToken);
  //
  //    ResponseCookie deleteCookie =
  //        ResponseCookie.from("refreshToken", "")
  //            .httpOnly(true)
  //            .secure(true)
  //            .sameSite("Strict")
  //            .path("/")
  //            .maxAge(0)
  //            .build();
  //
  //    return ResponseEntity.ok()
  //        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
  //        .body(new BaseResponseDTO<>(response, null));
  //  }
}
