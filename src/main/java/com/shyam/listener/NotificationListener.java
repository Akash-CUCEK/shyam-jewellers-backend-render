package com.shyam.listener;

import com.shyam.dto.NotificationMessage;
import com.shyam.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

  private final EmailService emailService;

  public NotificationListener(EmailService emailService) {
    this.emailService = emailService;
  }

  @RetryableTopic(
      attempts = "4",
      backoff = @Backoff(delay = 5000, multiplier = 2),
      include = {Exception.class})
  @KafkaListener(
      topics = "${app.kafka.topic.notifications}",
      groupId = "${app.kafka.group.id.notifications}")
  public void listen(NotificationMessage notificationMessage) {
    try {
      log.info("Received notification message: {}", notificationMessage);
      processNotification(notificationMessage);
      log.info("Successfully processed notification message");
    } catch (Exception e) {
      log.error("Error processing notification message: {}", e.getMessage(), e);
      throw new RuntimeException("Notification processing failed", e);
    }
  }

  private void processNotification(NotificationMessage notificationMessage) {
    switch (notificationMessage.type()) {
      case WELCOME:
        emailService.sendWelcomeEmail(notificationMessage.to(), notificationMessage.cc());
        break;
      case UPDATE:
        emailService.sendUpdateEmail(notificationMessage.to(), notificationMessage.cc());
        break;
      case LOGIN:
        if (notificationMessage.otp() == null) {
          throw new IllegalArgumentException("OTP is required for login notification");
        }
        emailService.sendLoginOtp(
            notificationMessage.to(), notificationMessage.cc(), notificationMessage.otp());
        break;
      case REGISTER:
        if (notificationMessage.otp() == null) {
          throw new IllegalArgumentException("OTP is required for registration notification");
        }
        emailService.sendRegistrationEmail(
            notificationMessage.to(), notificationMessage.cc(), notificationMessage.otp());
        break;
    }
  }
}
