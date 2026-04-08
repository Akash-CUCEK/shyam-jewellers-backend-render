package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRepairRequestDTO {
  private String email;
  private String name;
  private String address;
  private String mobileNumber;
  private String notes;
}
