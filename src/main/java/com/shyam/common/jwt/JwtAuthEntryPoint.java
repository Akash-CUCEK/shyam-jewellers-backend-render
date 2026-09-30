package com.shyam.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.exception.dto.ErrorMessagesDTO;
import com.shyam.common.exception.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
          HttpServletRequest request,
          HttpServletResponse response,
          AuthenticationException authException)
          throws IOException {

    ErrorMessagesDTO errorMessagesDTO =
            new ErrorMessagesDTO(authException.getMessage());

    ErrorResponseDTO errorResponseDTO =
            new ErrorResponseDTO(
                    Collections.singletonList(errorMessagesDTO),
                    LocalDateTime.now(),
                    SYMErrorType.GENERIC_EXCEPTION,
                    "UNAUTHORIZED",
                    authException.getMessage());

    BaseResponseDTO<Void> baseResponse =
            new BaseResponseDTO<>(null, errorResponseDTO);

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    response
            .getWriter()
            .write(objectMapper.writeValueAsString(baseResponse));
  }
}
