package com.shyam.dao;

import com.shyam.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByTokenHash(String tokenHash);

  Optional<RefreshToken> findByEmailAndRole(String email, String role);
}
