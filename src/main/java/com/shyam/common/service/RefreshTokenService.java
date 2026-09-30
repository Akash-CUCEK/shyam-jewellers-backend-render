package com.shyam.common.service;

import com.shyam.common.dto.RefreshTokenDetails;
import com.shyam.common.service.Imp.TokenReuseDetectedException;
import com.shyam.common.util.TokenHashUtil;
import com.shyam.dao.RefreshTokenRepository;
import com.shyam.entity.RefreshToken;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  // single source of truth for token lifetime - keep the cookie maxAge in
  // RefreshTokenController.COOKIE_MAX_AGE in sync with this
  public static final Duration TOKEN_TTL = Duration.ofHours(5);

  /**
   * Stores a brand-new refresh token for a user + device.
   * Does NOT touch that user's other devices - each clientType/device keeps
   * its own independent row, so a mobile login no longer kills a web session.
   * Call this at login AND after every successful rotation.
   */
  public void store(String email, String role, String clientType, String rawToken) {
    RefreshToken entity = new RefreshToken();
    entity.setEmail(email);
    entity.setRole(role);
    entity.setClientType(clientType);
    entity.setTokenHash(TokenHashUtil.sha256Hex(rawToken));
    entity.setIssuedAt(Instant.now());
    entity.setExpiryDate(Instant.now().plus(TOKEN_TTL));
    entity.setRevoked(false);
    refreshTokenRepository.save(entity);
  }

  /**
   * Validates a raw refresh token and returns who it belongs to.
   *
   * @return details if valid; null if the token is unknown or expired
   *     (respond with a plain 401 in both cases)
   * @throws TokenReuseDetectedException if the token was found but already
   *     marked revoked - i.e. it was rotated once before and is now being
   *     replayed. All of this user's sessions are revoked as a side effect
   *     before this is thrown; the caller should just treat it as a 401.
   */
  public RefreshTokenDetails validate(String rawToken) {
    var hash = TokenHashUtil.sha256Hex(rawToken);
    Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash(hash);

    if (found.isEmpty()) {
      return null;
    }

    RefreshToken entity = found.get();

    if (entity.isRevoked()) {
      log.warn(
              "Refresh token reuse detected for {} ({}) on {} - revoking all sessions",
              entity.getEmail(),
              entity.getRole(),
              entity.getClientType());
      refreshTokenRepository.revokeAllForUser(entity.getEmail(), entity.getRole());
      throw new TokenReuseDetectedException(entity.getEmail(), entity.getRole());
    }

    if (entity.getExpiryDate().isBefore(Instant.now())) {
      return null;
    }

    return new RefreshTokenDetails(entity.getRole(), entity.getEmail());
  }

  /**
   * Marks a token revoked instead of deleting it, so that IF it gets replayed
   * later, validate() can tell the difference between "never existed" and
   * "already used" - only the latter is a theft signal worth reacting to.
   */
  @Transactional
  public void markRevoked(String rawToken) {
    String hash = TokenHashUtil.sha256Hex(rawToken);
    refreshTokenRepository
            .findByTokenHash(hash)
            .ifPresent(
                    t -> {
                      t.setRevoked(true);
                      refreshTokenRepository.save(t);
                    });
  }

  /** Full logout everywhere - call this from a "logout all devices" endpoint too. */
  @Transactional
  public void revokeAllForUser(String email, String role) {
    refreshTokenRepository.revokeAllForUser(email, role);
  }

  @Scheduled(cron = "0 0 3 * * *") // roz raat 3 baje
  @jakarta.transaction.Transactional
  public void purgeStaleTokens() {
    refreshTokenRepository.deleteAllByExpiryDateBeforeAndRevokedTrue(Instant.now());
    // ya simply: deleteAllByExpiryDateBefore(Instant.now()) - jo bhi expire ho chuka hai
  }
}