package com.shyam.payment.mapper;

import com.shyam.payment.dto.response.PaymentResponseDTO;
import com.shyam.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

  public PaymentResponseDTO toResponse(Payment payment) {
    return PaymentResponseDTO.builder()
        .paymentId(payment.getId())
        .paymentReference(payment.getPaymentReference())
        .orderId(payment.getOrder().getId())
        .amount(payment.getAmount())
        .currency(payment.getCurrency())
        .status(payment.getStatus())
        .paymentMethod(payment.getPaymentMethod())
        .gateway(payment.getGateway())
        .gatewayOrderId(payment.getGatewayOrderId())
        .gatewayPaymentId(payment.getGatewayPaymentId())
        .gatewayReferenceId(payment.getGatewayReferenceId())
        .paymentUrl(payment.getPaymentUrl())
        .failureCode(payment.getFailureCode())
        .failureReason(payment.getFailureReason())
        .expiresAt(payment.getExpiresAt())
        .paidAt(payment.getPaidAt())
        .createdAt(payment.getCreatedAt())
        .updatedAt(payment.getUpdatedAt())
        .build();
  }
}
