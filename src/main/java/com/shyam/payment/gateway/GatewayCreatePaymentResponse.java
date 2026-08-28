package com.shyam.payment.gateway;

public record GatewayCreatePaymentResponse(
    String gatewayOrderId, String gatewayReferenceId, String paymentUrl, String rawResponse) {}
