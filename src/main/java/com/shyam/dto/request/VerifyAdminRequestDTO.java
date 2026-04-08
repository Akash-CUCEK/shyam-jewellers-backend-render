package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyAdminRequestDTO {
  private String email;
  private String otp;
  private String password;
}
