package com.shyam.payment.gateway;

import com.shyam.common.constants.PaymentStatus;

public record GatewayRefundResult(
    PaymentStatus status, String gatewayRefundId, String rawResponse, String failureReason) {}
