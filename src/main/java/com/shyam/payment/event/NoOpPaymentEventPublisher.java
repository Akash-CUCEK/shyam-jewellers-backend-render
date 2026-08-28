package com.shyam.payment.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoOpPaymentEventPublisher implements PaymentEventPublisher {

  @Override
  public void publish(PaymentEvent event) {
    log.debug(
        "Payment event extension point invoked for paymentReference={} status={}",
        event.paymentReference(),
        event.status());
  }
}
