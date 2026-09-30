package com.shyam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {@Index(name = "idx_email_role_client", columnList = "email,role,clientType")})
@Getter
@Setter
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String role;

  // WEB / MOBILE (or a real deviceId) - lets the same user hold independent
  // sessions on multiple devices at once, instead of one wiping the other out
  @Column(nullable = false)
  private String clientType;

  // SHA-256 hex of the raw token - deterministic, so we can look it up
  // directly instead of loading a candidate row and comparing
  @Column(nullable = false, unique = true, length = 64)
  private String tokenHash;

  @Column(nullable = false)
  private Instant issuedAt;

  @Column(nullable = false)
  private Instant expiryDate;

  // Rows are kept (not deleted) after rotation and just flagged revoked, so
  // a replay of an already-used token can be detected as theft
  @Column(nullable = false)
  private boolean revoked = false;
}