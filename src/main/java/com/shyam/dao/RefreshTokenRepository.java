package com.shyam.dao;

import com.shyam.entity.RefreshToken;

import java.time.Instant;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  // Primary lookup path now - deterministic hash means we don't need
  // email/role from the client to find the row
  Optional<RefreshToken> findByTokenHash(String tokenHash);

  Optional<RefreshToken> findByEmailAndRoleAndClientTypeAndRevokedFalse(
          String email, String role, String clientType);

  // Used when reuse of an already-rotated token is detected - kills every
  // session this user has, on every device
  @Modifying
  @Query("update RefreshToken t set t.revoked = true where t.email = :email and t.role = :role")
  void revokeAllForUser(@Param("email") String email, @Param("role") String role);


  void deleteAllByExpiryDateBeforeAndRevokedTrue(Instant now);
}