package com.shyam.payment.event;

public interface PaymentEventPublisher {

  void publish(PaymentEvent event);
}
