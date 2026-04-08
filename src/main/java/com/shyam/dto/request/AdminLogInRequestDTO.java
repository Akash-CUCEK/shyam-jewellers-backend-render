package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLogInRequestDTO {
  private String email;
  private String password;
  private String deviceId;
}
