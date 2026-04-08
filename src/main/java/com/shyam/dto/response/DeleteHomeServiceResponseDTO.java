package com.shyam.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteHomeServiceResponseDTO implements Serializable {
  private String Response;
}
