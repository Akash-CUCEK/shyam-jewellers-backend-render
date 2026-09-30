package com.shyam.service.Imp;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.dto.NotificationMessage;
import com.shyam.service.EmailService;
import com.shyam.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
            throw new SYMException(
                HttpStatus.BAD_REQUEST,
                SYMErrorType.VALIDATION_FAILED,
                "OTP_REQUIRED",
                "OTP is required for login notification",
                "OTP is required for login notification");
          }
          emailService.sendLoginOtp(message.to(), message.cc(), message.otp());
          break;
        case REGISTER:
          if (message.otp() == null) {
            throw new SYMException(
                HttpStatus.BAD_REQUEST,
                SYMErrorType.VALIDATION_FAILED,
                "OTP_REQUIRED",
                "OTP is required for registration notification",
                "OTP is required for registration notification");
          }
          emailService.sendRegistrationEmail(message.to(), message.cc(), message.otp());
          break;
        default:
          log.warn("Unknown notification type: {}", message.type());
          throw new SYMException(
              HttpStatus.BAD_REQUEST,
              SYMErrorType.VALIDATION_FAILED,
              "UNKNOWN_NOTIFICATION_TYPE",
              "Unknown notification type: " + message.type(),
              "Unknown notification type");
      }
      log.info("Processed notification via email. type={}, to={}", message.type(), message.to());
    } catch (Exception e) {
      log.error("Failed to process notification. type={}, to={}", message.type(), message.to(), e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          "NOTIFICATION_PROCESSING_FAILED",
          "Failed to process notification",
          "Failed to process notification");
    }
  }
}
