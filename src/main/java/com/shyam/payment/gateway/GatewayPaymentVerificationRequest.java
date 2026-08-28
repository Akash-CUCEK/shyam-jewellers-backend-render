package com.shyam.payment.gateway;

import java.math.BigDecimal;

public record GatewayPaymentVerificationRequest(
    String gatewayOrderId,
    String gatewayPaymentId,
    String signature,
    String gatewayStatus,
    BigDecimal amount,
    String rawPayload) {}
