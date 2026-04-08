package com.shyam.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAllAdminResponseDTO {
  private Long id;
  private String name;
  private String email;
  private String phoneNumber;
}
