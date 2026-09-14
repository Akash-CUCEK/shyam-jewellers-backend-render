package com.shyam.service.Imp;

import com.shyam.dto.NotificationMessage;
import com.shyam.service.EmailService;
import com.shyam.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

  private final EmailService emailService;

  @Override
  public void process(NotificationMessage message) {
    try {
      switch (message.type()) {
        case WELCOME:
          emailService.sendWelcomeEmail(message.to(), message.cc());
          break;
        case UPDATE:
          emailService.sendUpdateEmail(message.to(), message.cc());
          break;
        case LOGIN:
          if (message.otp() == null) {
            throw new IllegalArgumentException("OTP is required for login notification");
          }
          emailService.sendLoginOtp(message.to(), message.cc(), message.otp());
          break;
        case REGISTER:
          if (message.otp() == null) {
            throw new IllegalArgumentException("OTP is required for registration notification");
          }
          emailService.sendRegistrationEmail(message.to(), message.cc(), message.otp());
          break;
        default:
          log.warn("Unknown notification type: {}", message.type());
      }
      log.info("Processed notification via email. type={}, to={}", message.type(), message.to());
    } catch (Exception e) {
      log.error("Failed to process notification. type={}, to={}", message.type(), message.to(), e);
      throw new RuntimeException("Failed to process notification", e);
    }
  }
}
