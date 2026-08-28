package com.shyam.common.jwt;

import com.shyam.common.redis.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

  private final TokenBlacklistService tokenBlacklistService;
  private static SecretKey secretKey;

  @Value("${jwt.secret}")
  private String jwtSecret;

  private static final long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;

  @PostConstruct
  void init() {
    if (jwtSecret == null || jwtSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
      throw new IllegalStateException("jwt.secret must be configured and at least 32 bytes long");
    }
    secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  public static String generateAccessToken(String username, String role) {
    return Jwts.builder()
        .setSubject(username)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
        .signWith(getSecretKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public static String generateRefreshToken() {
    return UUID.randomUUID().toString();
  }

  public boolean validateToken(String token) {
    try {
      if (tokenBlacklistService.isTokenBlacklisted(token)) {
        log.warn("Token is blacklisted: {}", token);
        return false;
      }
      Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  public static String getUsername(String token) {
    return getClaims(token).getSubject();
  }

  public static String getRole(String token) {
    return getClaims(token).get("role", String.class);
  }

  public static Date getExpiry(String token) {
    return getClaims(token).getExpiration();
  }

  private static Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private static SecretKey getSecretKey() {
    if (secretKey == null) {
      throw new IllegalStateException("JWT secret key has not been initialized");
    }
    return secretKey;
  }
}
