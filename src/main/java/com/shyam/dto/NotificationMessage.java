package com.shyam.dto;

public record NotificationMessage(String to, String cc, String otp, NotificationType type) {}
