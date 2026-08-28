package com.shyam.payment.entity;

import com.shyam.common.constants.PaymentGateway;
import com.shyam.common.constants.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
    name = "payment_gateway_responses",
    indexes = {
      @Index(name = "idx_payment_gateway_responses_payment_id", columnList = "payment_id"),
      @Index(name = "idx_payment_gateway_responses_gateway_order", columnList = "gateway_order_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGatewayResponse {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_gateway_responses_seq")
  @SequenceGenerator(
      name = "payment_gateway_responses_seq",
      sequenceName = "payment_gateway_responses_seq",
      allocationSize = 1)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private PaymentGateway gateway;

  @Column(name = "event_type", nullable = false, length = 80)
  private String eventType;

  @Enumerated(EnumType.STRING)
  @Column(length = 40)
  private PaymentStatus status;

  @Column(name = "gateway_order_id", length = 140)
  private String gatewayOrderId;

  @Column(name = "gateway_payment_id", length = 140)
  private String gatewayPaymentId;

  @Column(name = "signature", length = 500)
  private String signature;

  @Column(name = "signature_valid")
  private Boolean signatureValid;

  @Lob
  @Column(name = "raw_payload")
  private String rawPayload;

  @Column(name = "received_at", nullable = false)
  private LocalDateTime receivedAt;

  @PrePersist
  public void prePersist() {
    receivedAt = LocalDateTime.now();
  }
}
