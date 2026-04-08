package com.shyam.common.exception.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class SYMException extends RuntimeException {
  private HttpStatus status;
  private SYMErrorType errorType;
  private String errorCode;
  private String message;
  private String detailedMessage;

  public SYMException() {
    super();
  }

  public SYMException(
      HttpStatus status,
      SYMErrorType errorType,
      String errorCode,
      String message,
      String detailedMessage) {
    super(message);
    this.status = status;
    this.errorType = errorType;
    this.errorCode = errorCode;
    this.message = message;
    this.detailedMessage = detailedMessage;
  }
}
