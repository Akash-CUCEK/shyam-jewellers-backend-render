package com.shyam.common.redis.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String PREFIX = "blacklisted_token:";

  public void blacklistToken(String token, long expiryInSeconds) {
    redisTemplate.opsForValue().set(PREFIX + token, "true", Duration.ofSeconds(expiryInSeconds));
    log.info("Blacklisted access token with expiry={}s", expiryInSeconds);
  }

  public boolean isTokenBlacklisted(String token) {
    boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    log.debug("Token={} isBlacklisted={}", token, exists);
    return exists;
  }
}
