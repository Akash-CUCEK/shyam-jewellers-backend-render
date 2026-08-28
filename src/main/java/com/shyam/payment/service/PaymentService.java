package com.shyam.payment.service;

import com.shyam.payment.dto.request.*;
import com.shyam.payment.dto.response.PaymentHistoryResponseDTO;
import com.shyam.payment.dto.response.PaymentResponseDTO;
import com.shyam.payment.dto.response.PaymentVerificationResponseDTO;

public interface PaymentService {

  PaymentResponseDTO createPayment(CreatePaymentRequestDTO request, String idempotencyKeyHeader);

  PaymentVerificationResponseDTO verifyPayment(VerifyPaymentRequestDTO request);

  PaymentResponseDTO handleFailure(PaymentFailureRequestDTO request);

  PaymentResponseDTO retryPayment(
      Long paymentId, RetryPaymentRequestDTO request, String idempotencyKeyHeader);

  PaymentResponseDTO cancelPayment(Long paymentId, CancelPaymentRequestDTO request);

  PaymentResponseDTO refundPayment(RefundPaymentRequestDTO request, String idempotencyKeyHeader);

  PaymentResponseDTO getPayment(Long paymentId);

  PaymentResponseDTO getLatestPaymentForOrder(Long orderId);

  PaymentHistoryResponseDTO getPaymentHistory(Long orderId);
}
