package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordRequestDTO {
  private String email;
  private String password;
  private String newPassword;
}
