package com.shyam.common.util;

import com.shyam.common.dto.RefreshTokenBearer;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.service.CookieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

@Slf4j
public class AuthResponseHelper {

  public static <T extends RefreshTokenBearer> ResponseEntity<BaseResponseDTO<T>> handleResponse(
      String clientType,
      ResponseEntity<BaseResponseDTO<T>> responseEntity,
      CookieService cookieService) {

    if (clientType == null || clientType.isBlank()) {
      clientType = "WEB";
    }

    // MOBILE: as-is return, refreshToken body me rahega, koi cookie nahi
    if ("MOBILE".equalsIgnoreCase(clientType)) {
      return responseEntity;
    }

    // WEB: cookie set karo, body se refreshToken hatao
    BaseResponseDTO<T> baseResponseDto = responseEntity.getBody();
    if (baseResponseDto == null || baseResponseDto.getResponse() == null) {
      return responseEntity;
    }

    T dto = baseResponseDto.getResponse();
    String refreshToken = dto.getRefreshToken();

    if (refreshToken == null || refreshToken.isBlank()) {
      log.warn("No refresh token found in response DTO of type {}", dto.getClass().getSimpleName());
      return responseEntity;
    }

    ResponseCookie cookie =
        cookieService.createSecureCookie(
            "refreshToken", refreshToken, (int) java.time.Duration.ofDays(1).getSeconds());

    // Body se refresh token hatao (security — WEB me sirf cookie me rahe)
    dto.setRefreshToken(null);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.status(responseEntity.getStatusCode())
        .headers(headers)
        .body(baseResponseDto);
  }
}
