package com.shyam.dto.response;

import com.shyam.common.constants.Status;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairRequestResponseDTO implements Serializable {
  private Long serviceId;
  private String name;
  private String address;
  private String notes;
  private String mobileNumber;
  private LocalDateTime createdAt;
  private Status status;
}
