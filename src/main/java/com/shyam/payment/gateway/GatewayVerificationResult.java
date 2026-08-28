package com.shyam.payment.gateway;

import com.shyam.common.constants.PaymentStatus;

public record GatewayVerificationResult(
    boolean signatureValid,
    PaymentStatus status,
    String gatewayPaymentId,
    String rawResponse,
    String failureCode,
    String failureReason) {}
