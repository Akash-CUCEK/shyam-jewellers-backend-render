package com.shyam.common.util;

import com.shyam.common.constants.OrderStatus;
import com.shyam.common.constants.PaymentMethod;
import com.shyam.common.constants.PaymentStatus;
import com.shyam.common.constants.Role;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtil {

  public Long parseCreatedById(String createdBy) {
    return (createdBy != null && !createdBy.isEmpty()) ? Long.parseLong(createdBy) : null;
  }

  public OrderStatus parseOrderStatus(String status) {
    try {
      return OrderStatus.valueOf(status.toUpperCase());
    } catch (Exception e) {
      return null;
    }
  }

  public PaymentStatus parsePaymentStatus(String status) {
    try {
      return PaymentStatus.valueOf(status.toUpperCase());
    } catch (Exception e) {
      return null;
    }
  }

  public PaymentMethod parsePaymentMethod(String method) {
    try {
      return PaymentMethod.valueOf(method.toUpperCase());
    } catch (Exception e) {
      return null;
    }
  }

  public Role parseRole(String role) {
    try {
      return Role.valueOf(role.toUpperCase());
    } catch (Exception e) {
      return null;
    }
  }
}
