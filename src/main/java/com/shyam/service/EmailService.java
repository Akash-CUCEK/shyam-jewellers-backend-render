package com.shyam.service;

/** Service for sending emails. */
public interface EmailService {

  /**
   * Sends a login OTP email.
   *
   * @param to the recipient email address
   * @param cc the CC email address (optional, can be null or blank)
   * @param otp the OTP to include in the email
   */
  void sendLoginOtp(String to, String cc, String otp);

  /**
   * Sends a welcome email.
   *
   * @param to the recipient email address
   * @param cc the CC email address (optional, can be null or blank)
   */
  void sendWelcomeEmail(String to, String cc);

  /**
   * Sends a registration OTP email.
   *
   * @param to the recipient email address
   * @param cc the CC email address (optional, can be null or blank)
   * @param otp the OTP to include in the email
   */
  void sendRegistrationEmail(String to, String cc, String otp);

  void sendUpdateEmail(String s, String cc);
}
