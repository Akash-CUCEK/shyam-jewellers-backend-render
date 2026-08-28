package com.shyam.common.service;

import com.shyam.common.dto.RefreshTokenDetails;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RedisTemplate<String, String> redisTemplate;
  private final PasswordEncoder passwordEncoder;

  private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(1);

  private String buildKey(String email, String role) {
    return "refresh:" + role + ":" + email + ":";
  }

  public void store(String email, String role, String refreshToken) {
    String key = buildKey(email, role);
    String hashedToken = passwordEncoder.encode(refreshToken);
    redisTemplate.opsForValue().set(key, hashedToken, REFRESH_TOKEN_TTL);
  }

  public RefreshTokenDetails validate(String refreshToken, String email, String role) {
    String key = buildKey(email, role);
    String storedHash = redisTemplate.opsForValue().get(key);

    if (storedHash != null && passwordEncoder.matches(refreshToken, storedHash)) {
      return new RefreshTokenDetails(role, email);
    }
    return null;
  }

  public void delete(String email, String role) {
    redisTemplate.delete(buildKey(email, role));
  }
}
