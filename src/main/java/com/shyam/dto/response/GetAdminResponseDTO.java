package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAdminResponseDTO implements Serializable {
  private String name;
  private String phoneNumber;
  private String imageUrl;
}
