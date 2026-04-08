package com.shyam.dto.response;

import com.shyam.common.constants.ServiceType;
import com.shyam.common.constants.Status;
import java.io.Serializable;
import java.time.LocalDateTime;

public class SearchHomeServiceResponseDTO implements Serializable {
  private Long serviceId;
  private String name;
  private String phoneNumber;
  private ServiceType serviceType;
  private LocalDateTime createdAt;
  private Status status;
}
