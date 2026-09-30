package com.shyam.dto.response;

import com.shyam.entity.MaterialType;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMaterialTypeResponseDTO {
  private Long materialTypeId;
  private String name;
  private String createdBy;
  private LocalDateTime createdAt;
  private String updatedBy;
  private LocalDateTime updatedAt;
  private Boolean status;

  public static GetMaterialTypeResponseDTO fromEntity(MaterialType materialType) {
    return GetMaterialTypeResponseDTO.builder()
        .materialTypeId(materialType.getMaterialTypeId())
        .name(materialType.getName())
        .createdBy(materialType.getCreatedBy())
        .createdAt(materialType.getCreatedAt())
        .updatedBy(materialType.getUpdatedBy())
        .updatedAt(materialType.getUpdatedAt())
        .status(materialType.getStatus())
        .build();
  }
}
