package com.shyam.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shyam.common.exception.domain.SYMErrorType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Generated;

public class ErrorResponseDTO implements Serializable {
  private List<ErrorMessagesDTO> messages;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime timestamp;

  private SYMErrorType errorType;
  private String errorCode;
  private String detailedMessage;

  @Generated
  public void setMessages(final List<ErrorMessagesDTO> messages) {
    this.messages = messages;
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  @Generated
  public void setTimestamp(final LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Generated
  public void setErrorType(final SYMErrorType errorType) {
    this.errorType = errorType;
  }

  @Generated
  public List<ErrorMessagesDTO> getMessages() {
    return this.messages;
  }

  @Generated
  public LocalDateTime getTimestamp() {
    return this.timestamp;
  }

  @Generated
  public SYMErrorType getErrorType() {
    return this.errorType;
  }

  @Generated
  public String getErrorCode() {
    return this.errorCode;
  }

  @Generated
  public void setErrorCode(final String errorCode) {
    this.errorCode = errorCode;
  }

  @Generated
  public String getDetailedMessage() {
    return this.detailedMessage;
  }

  @Generated
  public void setDetailedMessage(final String detailedMessage) {
    this.detailedMessage = detailedMessage;
  }

  @Generated
  public ErrorResponseDTO(
      final List<ErrorMessagesDTO> messages,
      final LocalDateTime timestamp,
      final SYMErrorType errorType,
      final String errorCode,
      final String detailedMessage) {
    this.messages = messages;
    this.timestamp = timestamp;
    this.errorType = errorType;
    this.errorCode = errorCode;
    this.detailedMessage = detailedMessage;
  }
}
