package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyForgetPasswordRequestDTO {
  private String otp;
}
