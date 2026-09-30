package com.shyam.dto.response;

import com.shyam.entity.MaterialType;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMaterialTypeResponseDTO {

  private Long materialTypeId;
  private String name;
  private String updatedBy;
  private LocalDateTime updatedAt;
  private Boolean status;

  public static UpdateMaterialTypeResponseDTO fromEntity(MaterialType materialType) {
    return UpdateMaterialTypeResponseDTO.builder()
        .materialTypeId(materialType.getMaterialTypeId())
        .name(materialType.getName())
        .updatedBy(materialType.getUpdatedBy())
        .updatedAt(materialType.getUpdatedAt())
        .status(materialType.getStatus())
        .build();
  }
}
