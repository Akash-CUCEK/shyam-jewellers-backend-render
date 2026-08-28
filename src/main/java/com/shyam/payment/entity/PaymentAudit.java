package com.shyam.payment.entity;

import com.shyam.common.constants.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
    name = "payment_audits",
    indexes = {
      @Index(name = "idx_payment_audits_payment_id", columnList = "payment_id"),
      @Index(name = "idx_payment_audits_created_at", columnList = "created_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_audits_seq")
  @SequenceGenerator(
      name = "payment_audits_seq",
      sequenceName = "payment_audits_seq",
      allocationSize = 1)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  @Column(nullable = false, length = 80)
  private String action;

  @Enumerated(EnumType.STRING)
  @Column(name = "from_status", length = 40)
  private PaymentStatus fromStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "to_status", length = 40)
  private PaymentStatus toStatus;

  @Column(length = 120)
  private String actor;

  @Column(length = 1000)
  private String message;

  @Lob private String metadata;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    createdAt = LocalDateTime.now();
  }
}
