package com.shyam.common.jwt;

import com.shyam.common.redis.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

  private final TokenBlacklistService tokenBlacklistService;
  private static final SecretKey SECRET_KEY =
      Keys.hmacShaKeyFor(JwtConstants.SECRET.getBytes(StandardCharsets.UTF_8));

  private static final long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;

  public static String generateAccessToken(String username, String role) {
    return Jwts.builder()
        .setSubject(username)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
        .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
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
      Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
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
    return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
  }
}
