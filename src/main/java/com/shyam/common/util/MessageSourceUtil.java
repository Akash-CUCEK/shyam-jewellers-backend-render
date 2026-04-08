package com.shyam.common.util;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceUtil {

  private final MessageSource messageSource;

  @Autowired
  public MessageSourceUtil(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public String getMessage(String code) {
    return messageSource.getMessage(code, null, Locale.getDefault());
  }

  public String getMessage(String code, Object[] args) {
    return messageSource.getMessage(code, args, Locale.getDefault());
  }
}
