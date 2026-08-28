package com.shyam.service;

import com.shyam.dto.NotificationMessage;

public interface NotificationService {

  void process(NotificationMessage message);
}
