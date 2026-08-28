package com.shyam.payment.config;

import com.shyam.common.constants.PaymentGateway;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

  private String currency = "INR";
  private Integer requestTtlMinutes = 20;
  private Gateway gateway = new Gateway();
  private Urls urls = new Urls();

  @Getter
  @Setter
  public static class Gateway {
    private PaymentGateway provider = PaymentGateway.MOCK;
    private String signatureSecret;
    private String checkoutBaseUrl = "https://payments.example.local/checkout";
  }

  @Getter
  @Setter
  public static class Urls {
    private String callbackUrl;
    private String successUrl;
    private String failureUrl;
  }
}
