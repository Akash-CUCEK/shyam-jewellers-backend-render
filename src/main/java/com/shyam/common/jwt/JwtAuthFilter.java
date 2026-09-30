package com.shyam.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.exception.dto.ErrorMessagesDTO;
import com.shyam.common.exception.dto.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final UserDetailsServiceImpl userDetailsService;
  private final JwtUtil jwtUtil;

  // Use Spring Boot's configured ObjectMapper
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
          HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    String uri = request.getRequestURI();

    // =========================================================
    // PUBLIC ENDPOINTS
    // =========================================================

    if (uri.contains("/refreshToken")
            || uri.contains("/api/v1/auth/login")
            || uri.contains("/api/v1/auth/verify")
            || uri.contains("/verifyLoginOtp")
            || uri.contains("/auth/api/v1/admin/initiateLogin")
            || uri.contains("/auth/api/v1/admin/verifyLoginOtp")
            || uri.startsWith("/api/v1/public/")) {

      filterChain.doFilter(request, response);
      return;
    }

    // =========================================================
    // AUTHORIZATION HEADER
    // =========================================================

    final String authHeader = request.getHeader("Authorization");

    log.info("========================================");
    log.info("➡️ Request URI: {}", uri);
    log.info("➡️ Authorization Header Present: {}", authHeader != null);
    log.info("========================================");

    try {

      // =========================================================
      // CHECK BEARER TOKEN
      // =========================================================

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {

        log.warn("⚠️ No Bearer token found");

        filterChain.doFilter(request, response);
        return;
      }

      // =========================================================
      // ALREADY AUTHENTICATED
      // =========================================================

      if (SecurityContextHolder.getContext().getAuthentication() != null) {

        filterChain.doFilter(request, response);
        return;
      }

      // =========================================================
      // EXTRACT JWT
      // =========================================================

      String jwt = authHeader.substring(7);

      // =========================================================
      // VALIDATE JWT
      // =========================================================

      if (!jwtUtil.validateToken(jwt)) {
        // Send unauthorized response for invalid/expired token
        sendUnauthorizedResponse(response, objectMapper);
        return;
      }

      // =========================================================
      // GET JWT DATA
      // =========================================================

      String username = JwtUtil.getUsername(jwt);
      String role = JwtUtil.getRole(jwt);

      log.info("✅ JWT VALID");
      log.info("👤 Username : {}", username);
      log.info("🔐 Role     : {}", role);

      // =========================================================
      // LOAD USER BASED ON ROLE
      // =========================================================

      UserDetails userDetails =
              userDetailsService.loadUserByUsername(username, role);

      // =========================================================
      // CREATE AUTHENTICATION
      // =========================================================

      UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                      userDetails,
                      null,
                      userDetails.getAuthorities());

      // =========================================================
      // SET SECURITY CONTEXT
      // =========================================================

      SecurityContextHolder.getContext().setAuthentication(authentication);

      log.info("🔓 Authentication successfully set");
      log.info("👤 Principal   : {}", userDetails.getUsername());
      log.info("🔐 Authorities : {}", userDetails.getAuthorities());
      log.info("========================================");

      // =========================================================
      // CONTINUE REQUEST
      // =========================================================

      filterChain.doFilter(request, response);

    } catch (Exception ex) {

      log.error("❌ JWT Filter error", ex);

      SecurityContextHolder.clearContext();

      // =========================================================
      // BUILD UNAUTHORIZED RESPONSE
      // =========================================================

      ErrorMessagesDTO errorMessagesDTO =
              new ErrorMessagesDTO("Unauthorized");

      ErrorResponseDTO errorResponseDTO =
              new ErrorResponseDTO(
                      Collections.singletonList(errorMessagesDTO),
                      LocalDateTime.now(),
                      SYMErrorType.GENERIC_EXCEPTION,
                      "UNAUTHORIZED",
                      "Unauthorized");

      BaseResponseDTO<Void> baseResponse =
              new BaseResponseDTO<>(null, errorResponseDTO);

      // =========================================================
      // SEND JSON RESPONSE
      // =========================================================

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      response
              .getWriter()
              .write(objectMapper.writeValueAsString(baseResponse));
    }
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, ObjectMapper objectMapper)
          throws IOException {
    log.info("🔑 Sending UNAUTHORIZED response due to invalid or expired token");

    // Build the same error response as in the catch block
    ErrorMessagesDTO errorMessagesDTO =
            new ErrorMessagesDTO("Unauthorized");

    ErrorResponseDTO errorResponseDTO =
            new ErrorResponseDTO(
                    Collections.singletonList(errorMessagesDTO),
                    LocalDateTime.now(),
                    SYMErrorType.GENERIC_EXCEPTION,
                    "UNAUTHORIZED",
                    "Unauthorized");

    BaseResponseDTO<Void> baseResponse =
            new BaseResponseDTO<>(null, errorResponseDTO);

    // Send JSON response
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    response
            .getWriter()
            .write(objectMapper.writeValueAsString(baseResponse));

    log.info("📤 UNAUTHORIZED response sent successfully");
  }
}
