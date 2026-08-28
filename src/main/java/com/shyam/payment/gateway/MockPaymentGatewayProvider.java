package com.shyam.payment.gateway;

import com.shyam.common.constants.PaymentGateway;
import com.shyam.common.constants.PaymentStatus;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.payment.config.PaymentProperties;
import com.shyam.payment.entity.Payment;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class MockPaymentGatewayProvider implements PaymentGatewayProvider {

  private static final String HMAC_ALGORITHM = "HmacSHA256";
  private final PaymentProperties paymentProperties;

  @Override
  public PaymentGateway gateway() {
    return PaymentGateway.MOCK;
  }

  @Override
  public GatewayCreatePaymentResponse createPayment(GatewayCreatePaymentRequest request) {
    String gatewayOrderId = "mock_order_" + request.paymentReference();
    String gatewayReferenceId = "mock_ref_" + request.paymentReference();
    String paymentUrl =
        UriComponentsBuilder.fromUriString(paymentProperties.getGateway().getCheckoutBaseUrl())
            .queryParam("paymentReference", request.paymentReference())
            .queryParam("orderId", request.orderId())
            .queryParam("amount", request.amount())
            .queryParam("currency", request.currency())
            .build()
            .toUriString();
    String rawResponse =
        "{\"gateway\":\"MOCK\",\"gatewayOrderId\":\""
            + gatewayOrderId
            + "\",\"gatewayReferenceId\":\""
            + gatewayReferenceId
            + "\"}";

    return new GatewayCreatePaymentResponse(
        gatewayOrderId, gatewayReferenceId, paymentUrl, rawResponse);
  }

  @Override
  public GatewayVerificationResult verifyPayment(
      Payment payment, GatewayPaymentVerificationRequest request) {
    boolean signatureValid =
        isValidSignature(request.gatewayOrderId(), request.gatewayPaymentId(), request.signature());
    PaymentStatus status = parseGatewayStatus(request.gatewayStatus());

    return new GatewayVerificationResult(
        signatureValid,
        status,
        request.gatewayPaymentId(),
        request.rawPayload(),
        status == PaymentStatus.FAILED ? "MOCK_PAYMENT_FAILED" : null,
        status == PaymentStatus.FAILED ? "Mock gateway reported payment failure" : null);
  }

  @Override
  public GatewayCancelResult cancelPayment(Payment payment, String reason) {
    String rawResponse =
        "{\"gateway\":\"MOCK\",\"action\":\"cancel\",\"paymentReference\":\""
            + payment.getPaymentReference()
            + "\"}";
    return new GatewayCancelResult(PaymentStatus.CANCELLED, rawResponse, null);
  }

  @Override
  public GatewayRefundResult refundPayment(
      Payment payment, BigDecimal amount, String reason, String idempotencyKey) {
    String refundId = "mock_refund_" + payment.getPaymentReference();
    String rawResponse =
        "{\"gateway\":\"MOCK\",\"action\":\"refund\",\"refundId\":\""
            + refundId
            + "\",\"amount\":\""
            + amount
            + "\"}";
    return new GatewayRefundResult(PaymentStatus.REFUNDED, refundId, rawResponse, null);
  }

  private boolean isValidSignature(
      String gatewayOrderId, String gatewayPaymentId, String signature) {
    if (signature == null || signature.isBlank()) {
      return false;
    }

    String secret = paymentProperties.getGateway().getSignatureSecret();
    if (secret == null || secret.isBlank()) {
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Payment gateway signature secret is not configured",
          "Configure PAYMENT_GATEWAY_SIGNATURE_SECRET before payment verification");
    }

    String payload = gatewayOrderId + "|" + gatewayPaymentId;
    String expected = hmacSha256(payload, secret);
    return constantTimeEquals(expected, signature);
  }

  private PaymentStatus parseGatewayStatus(String status) {
    if (status == null || status.isBlank()) {
      return PaymentStatus.PROCESSING;
    }

    try {
      return PaymentStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      return PaymentStatus.PROCESSING;
    }
  }

  private String hmacSha256(String payload, String secret) {
    try {
      Mac mac = Mac.getInstance(HMAC_ALGORITHM);
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
      return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Unable to verify payment signature",
          e.getMessage());
    }
  }

  private boolean constantTimeEquals(String expected, String actual) {
    byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
    byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);
    if (expectedBytes.length != actualBytes.length) {
      return false;
    }

    int result = 0;
    for (int i = 0; i < expectedBytes.length; i++) {
      result |= expectedBytes[i] ^ actualBytes[i];
    }
    return result == 0;
  }
}
