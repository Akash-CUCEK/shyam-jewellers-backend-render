package com.shyam.payment.event;

import com.shyam.common.constants.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentEvent(
    Long paymentId,
    String paymentReference,
    Long orderId,
    PaymentStatus status,
    BigDecimal amount,
    String currency,
    LocalDateTime occurredAt) {}
