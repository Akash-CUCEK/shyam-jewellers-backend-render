package com.shyam.payment.dto.response;

import com.shyam.common.constants.PaymentGateway;
import com.shyam.common.constants.PaymentMethod;
import com.shyam.common.constants.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

  private Long paymentId;
  private String paymentReference;
  private Long orderId;
  private BigDecimal amount;
  private String currency;
  private PaymentStatus status;
  private PaymentMethod paymentMethod;
  private PaymentGateway gateway;
  private String gatewayOrderId;
  private String gatewayPaymentId;
  private String gatewayReferenceId;
  private String paymentUrl;
  private String failureCode;
  private String failureReason;
  private LocalDateTime expiresAt;
  private LocalDateTime paidAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
