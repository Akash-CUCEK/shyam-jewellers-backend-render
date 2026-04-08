package com.shyam.dto.response;

import java.io.Serializable;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllRepairResponseDTO implements Serializable {
  private List<RepairRequestResponseDTO> getAllServices;
}
