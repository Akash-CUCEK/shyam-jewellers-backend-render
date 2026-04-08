package com.shyam.dto.response;

import java.io.Serializable;
import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetOrderByDateResponseDTO implements Serializable {
  private List<GetOrderByIdResponseDTO> getOrderByDateResponseDTOList;
}
