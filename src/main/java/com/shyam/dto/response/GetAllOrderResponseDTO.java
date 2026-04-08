package com.shyam.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAllOrderResponseDTO {
  private List<GetOrderByIdResponseDTO> getOrderByDateResponseDTOList;
}
