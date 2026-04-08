package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHomeServiceRequestDTO {
  private String name;
  private String email;
  private String address;
  private String phoneNumber;
  private String serviceType;
  private String notes;
}
