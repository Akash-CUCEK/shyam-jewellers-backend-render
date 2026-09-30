package com.shyam.dao;

import com.shyam.entity.BlacklistedToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
  Optional<BlacklistedToken> findByTokenHash(String tokenHash);
}
