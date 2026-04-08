package com.shyam.dto.request;

import com.shyam.common.constants.Status;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditRepairRequestDTO {
  private Long serviceId;
  private Status status;
  private String email;
  private String name;
  private String address;
  private String mobileNumber;
  private String notes;
}
