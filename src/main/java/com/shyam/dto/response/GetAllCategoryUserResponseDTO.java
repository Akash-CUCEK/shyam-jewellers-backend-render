package com.shyam.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllCategoryUserResponseDTO {
  private List<GetCategoryUserResponseDTO> getCategoryUserResponseDTOS;
}
