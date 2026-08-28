package com.shyam.service.Imp;

import com.shyam.dto.NotificationMessage;
import com.shyam.publisher.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements com.shyam.service.NotificationService {

  private final NotificationPublisher notificationPublisher;

  @Override
  public void process(NotificationMessage message) {
    try {
      notificationPublisher.publish(message);
      log.info(
          "Processed notification via publisher. type={}, to={}", message.type(), message.to());
    } catch (Exception e) {
      log.error("Failed to process notification. type={}, to={}", message.type(), message.to(), e);
      throw new RuntimeException("Failed to process notification", e);
    }
  }
}
