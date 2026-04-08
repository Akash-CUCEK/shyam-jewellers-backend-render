package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminLogoutResponseDTO implements Serializable {
  private String message;
}
