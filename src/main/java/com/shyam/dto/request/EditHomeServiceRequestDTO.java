package com.shyam.dto.request;

import com.shyam.common.constants.ServiceType;
import com.shyam.common.constants.Status;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditHomeServiceRequestDTO {
  private Long serviceId;
  private String name;
  private String address;
  private Status status;
  private String phoneNumber;
  private ServiceType serviceType;
  private String notes;
  private String updatedBy;
}
