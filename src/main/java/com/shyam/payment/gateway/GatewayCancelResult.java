package com.shyam.payment.gateway;

import com.shyam.common.constants.PaymentStatus;

public record GatewayCancelResult(PaymentStatus status, String rawResponse, String failureReason) {}
