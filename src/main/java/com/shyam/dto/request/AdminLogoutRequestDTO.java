package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminLogoutRequestDTO {
  private String token;
}
