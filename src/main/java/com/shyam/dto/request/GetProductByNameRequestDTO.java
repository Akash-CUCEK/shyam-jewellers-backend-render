package com.shyam.dto.request;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetProductByNameRequestDTO implements Serializable {
  private String name;
}
