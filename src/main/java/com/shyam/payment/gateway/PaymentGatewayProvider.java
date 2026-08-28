package com.shyam.payment.gateway;

import com.shyam.common.constants.PaymentGateway;
import com.shyam.payment.entity.Payment;
import java.math.BigDecimal;

public interface PaymentGatewayProvider {

  PaymentGateway gateway();

  GatewayCreatePaymentResponse createPayment(GatewayCreatePaymentRequest request);

  GatewayVerificationResult verifyPayment(
      Payment payment, GatewayPaymentVerificationRequest request);

  GatewayCancelResult cancelPayment(Payment payment, String reason);

  GatewayRefundResult refundPayment(
      Payment payment, BigDecimal amount, String reason, String idempotencyKey);
}
