package com.shyam.publisher;

import com.shyam.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

  private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

  @Value("${app.kafka.topic.notifications}")
  private String notificationTopic;

  public void publish(NotificationMessage message) {
    try {
      kafkaTemplate.send(notificationTopic, message.to(), message);
      log.info("Published notification to Kafka. type={}, to={}", message.type(), message.to());
    } catch (Exception e) {
      log.error(
          "Failed to publish notification to Kafka. type={}, to={}",
          message.type(),
          message.to(),
          e);

      throw new RuntimeException("Failed to publish notification", e);
    }
  }
}
