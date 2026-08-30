package com.shyam.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain)
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

        log.warn("❌ Invalid or expired JWT");

        filterChain.doFilter(request, response);
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
      log.info("👤 Principal    : {}", userDetails.getUsername());
      log.info("🔐 Authorities  : {}", userDetails.getAuthorities());
      log.info("========================================");

      // =========================================================
      // CONTINUE REQUEST
      // =========================================================

      filterChain.doFilter(request, response);

    } catch (Exception ex) {

      log.error("❌ JWT Filter error", ex);

      SecurityContextHolder.clearContext();

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");

      response
              .getWriter()
              .write("{\"message\":\"Unauthorized\"}");
    }
  }
}