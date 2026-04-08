package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditAdminRequestDTO {
  private String name;
  private String email;
  private String phoneNumber;
  private String imageUrl;
}
