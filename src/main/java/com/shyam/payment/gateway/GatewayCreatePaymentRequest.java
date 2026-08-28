package com.shyam.payment.gateway;

import com.shyam.common.constants.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GatewayCreatePaymentRequest(
    String paymentReference,
    Long orderId,
    BigDecimal amount,
    String currency,
    PaymentMethod paymentMethod,
    String customerEmail,
    String customerPhone,
    String callbackUrl,
    String successUrl,
    String failureUrl,
    LocalDateTime expiresAt) {}
