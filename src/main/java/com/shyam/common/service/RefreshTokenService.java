package com.shyam.common.service;

import com.shyam.common.dto.RefreshTokenDetails;
import com.shyam.dao.RefreshTokenRepository;
import com.shyam.entity.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;

  private String buildKey(String email, String role) {
    // This method is kept for compatibility but not used in JPA version.
    // We'll keep it in case it's used elsewhere, but we won't use it in store/validate/delete.
    return "refresh:" + role + ":" + email + ":";
  }

  public void store(String email, String role, String refreshToken) {
    // Remove any existing refresh token for this email and role
    refreshTokenRepository
        .findByEmailAndRole(email, role)
        .ifPresent(refreshTokenRepository::delete);

    RefreshToken tokenEntity = new RefreshToken();
    tokenEntity.setEmail(email);
    tokenEntity.setRole(role);
    tokenEntity.setTokenHash(passwordEncoder.encode(refreshToken));
    tokenEntity.setExpiryDate(Instant.now().plusSeconds(java.time.Duration.ofHours(5).toSeconds()));
    refreshTokenRepository.save(tokenEntity);
  }

  public RefreshTokenDetails validate(String refreshToken, String email, String role) {
    Optional<RefreshToken> optional = refreshTokenRepository.findByEmailAndRole(email, role);
    if (optional.isPresent()) {
      RefreshToken tokenEntity = optional.get();
      if (passwordEncoder.matches(refreshToken, tokenEntity.getTokenHash())
          && tokenEntity.getExpiryDate().isAfter(Instant.now())) {
        return new RefreshTokenDetails(role, email);
      }
    }
    return null;
  }

  public void delete(String email, String role) {
    refreshTokenRepository
        .findByEmailAndRole(email, role)
        .ifPresent(refreshTokenRepository::delete);
  }
}
