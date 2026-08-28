package com.shyam.service.Imp;

import com.shyam.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImp implements EmailService {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;

  @Override
  public void sendLoginOtp(String to, String cc, String otp) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(to);
      if (cc != null && !cc.isBlank()) {
        helper.setCc(cc);
      }
      helper.setSubject("Login OTP - Shyam Jewellers");

      Context context = new Context();
      context.setVariable("otp", otp);
      String htmlContent = templateEngine.process("email/login-otp", context);
      helper.setText(htmlContent, true); // true indicates HTML

      mailSender.send(message);
      log.info("Login OTP email sent successfully to {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send login OTP email to {}: {}", to, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void sendWelcomeEmail(String to, String cc) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(to);
      if (cc != null && !cc.isBlank()) {
        helper.setCc(cc);
      }
      helper.setSubject("Welcome to Shyam Jewellers");

      Context context = new Context();
      // We don't have a username from the NotificationListener, so we can use a default or leave it
      // out.
      // The template uses ${username} with a default of "Valued Customer" if not present.
      context.setVariable("username", "Valued Customer");
      String htmlContent = templateEngine.process("email/welcome-user", context);
      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("Welcome email sent successfully to {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send welcome email to {}: {}", to, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void sendRegistrationEmail(String to, String cc, String otp) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(to);
      if (cc != null && !cc.isBlank()) {
        helper.setCc(cc);
      }
      helper.setSubject("Registration OTP - Shyam Jewellers");

      Context context = new Context();
      context.setVariable("title", "Registration OTP - Shyam Jewellers");
      context.setVariable("headerText", "Registration One-Time Password");
      context.setVariable("otp", otp);
      String htmlContent = templateEngine.process("email/register-otp", context);
      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("Registration email sent successfully to {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send registration email to {}: {}", to, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void sendUpdateEmail(String s, String cc) {}
}
