package com.shyam.payment.entity;

import com.shyam.common.constants.PaymentStatus;
import com.shyam.common.constants.PaymentTransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
    name = "payment_transactions",
    indexes = {
      @Index(name = "idx_payment_transactions_payment_id", columnList = "payment_id"),
      @Index(name = "idx_payment_transactions_gateway_txn", columnList = "gateway_transaction_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_transactions_seq")
  @SequenceGenerator(
      name = "payment_transactions_seq",
      sequenceName = "payment_transactions_seq",
      allocationSize = 1)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false, length = 40)
  private PaymentTransactionType transactionType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private PaymentStatus status;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal amount;

  @Column(name = "gateway_order_id", length = 140)
  private String gatewayOrderId;

  @Column(name = "gateway_transaction_id", length = 140)
  private String gatewayTransactionId;

  @Column(name = "idempotency_key", length = 140)
  private String idempotencyKey;

  @Column(name = "signature_verified")
  private Boolean signatureVerified;

  @Lob
  @Column(name = "gateway_payload")
  private String gatewayPayload;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    createdAt = LocalDateTime.now();
  }
}
