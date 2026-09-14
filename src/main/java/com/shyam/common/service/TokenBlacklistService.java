package com.shyam.common.service;

import com.shyam.dao.BlacklistedTokenRepository;
import com.shyam.entity.BlacklistedToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public void blacklistToken(String token, long expiryInSeconds) {
        // We store the hash of the token for security
        // In practice, we might not want to hash the token again because the token is already a JWT and we have the raw token.
        // But to be consistent with the previous Redis version (which stored the raw token), we store the raw token.
        // However, note that the token is a JWT and can be long. We'll store it as is.
        // We'll also set an expiry based on the remaining time of the token.
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setTokenHash(token); // storing the raw token (not hashed) for simplicity and to match previous behavior
        blacklistedToken.setExpiryDate(Instant.now().plusSeconds(expiryInSeconds));
        blacklistedTokenRepository.save(blacklistedToken);
        log.info("Blacklisted token with expiry={}s", expiryInSeconds);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.findByTokenHash(token)
                .map(t -> t.getExpiryDate().isAfter(Instant.now()))
                .orElse(false);
    }
}