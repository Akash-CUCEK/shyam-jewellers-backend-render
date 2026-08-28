package com.shyam.notification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Email notification event for Kafka messaging */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationEvent implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Type of email event (e.g., LOGIN_OTP, WELCOME_USER, PASSWORD_CHANGED) */
  private String eventType;

  /** Primary recipient email address */
  private String to;

  /** CC recipients (optional) */
  private List<String> cc;

  /** Email subject */
  private String subject;

  /** Template name to use for email body generation */
  private String template;

  /** Template data for variable substitution */
  private Map<String, Object> data;

  /** Priority level (optional) */
  private String priority;

  /** Correlation ID for tracking (optional) */
  private String correlationId;
}
