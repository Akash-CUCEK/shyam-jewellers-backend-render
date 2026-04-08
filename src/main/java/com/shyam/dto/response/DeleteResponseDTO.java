package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteResponseDTO implements Serializable {
  private String message;
}
