package com.shyam.dto.response;

import java.io.Serializable;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetProductResponseDTO implements Serializable {
  private List<AllProductResponseDTO> products;
}
