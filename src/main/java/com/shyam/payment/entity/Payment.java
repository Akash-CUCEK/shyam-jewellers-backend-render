package com.shyam.payment.entity;

import com.shyam.common.constants.PaymentGateway;
import com.shyam.common.constants.PaymentMethod;
import com.shyam.common.constants.PaymentStatus;
import com.shyam.entity.Order;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(
    name = "payments",
    indexes = {
      @Index(name = "idx_payments_order_id", columnList = "order_id"),
      @Index(name = "idx_payments_status", columnList = "status"),
      @Index(name = "idx_payments_gateway_order_id", columnList = "gateway_order_id")
    },
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_payments_reference", columnNames = "payment_reference"),
      @UniqueConstraint(name = "uk_payments_idempotency_key", columnNames = "idempotency_key")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_seq")
  @SequenceGenerator(name = "payments_seq", sequenceName = "payments_seq", allocationSize = 1)
  private Long id;

  @Column(name = "payment_reference", nullable = false, length = 80)
  private String paymentReference;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private PaymentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false, length = 40)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private PaymentGateway gateway;

  @Column(name = "idempotency_key", length = 140)
  private String idempotencyKey;

  @Column(name = "gateway_order_id", length = 140)
  private String gatewayOrderId;

  @Column(name = "gateway_payment_id", length = 140)
  private String gatewayPaymentId;

  @Column(name = "gateway_reference_id", length = 140)
  private String gatewayReferenceId;

  @Column(name = "payment_url", length = 1000)
  private String paymentUrl;

  @Column(name = "failure_code", length = 100)
  private String failureCode;

  @Column(name = "failure_reason", length = 1000)
  private String failureReason;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  @Column(name = "failed_at")
  private LocalDateTime failedAt;

  @Column(name = "cancelled_at")
  private LocalDateTime cancelledAt;

  @Column(name = "refunded_at")
  private LocalDateTime refundedAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Version private Long version;

  @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PaymentTransaction> transactions = new ArrayList<>();

  @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PaymentAudit> audits = new ArrayList<>();

  @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PaymentGatewayResponse> gatewayResponses = new ArrayList<>();

  @PrePersist
  public void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public boolean isTerminal() {
    return status == PaymentStatus.SUCCESS
        || status == PaymentStatus.FAILED
        || status == PaymentStatus.CANCELLED
        || status == PaymentStatus.REFUNDED
        || status == PaymentStatus.EXPIRED;
  }

  public boolean isActive() {
    return status == PaymentStatus.CREATED
        || status == PaymentStatus.PENDING
        || status == PaymentStatus.PROCESSING
        || status == PaymentStatus.REFUND_INITIATED;
  }
}
