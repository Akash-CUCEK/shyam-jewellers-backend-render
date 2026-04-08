package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAddResponseDTO implements Serializable {
  private String name;
  private String message;
}
