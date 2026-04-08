package com.shyam.dto.response;

import java.io.Serializable;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllHomeServiceTypeResponseDTO implements Serializable {
  private List<String> serviceTypes;
}
