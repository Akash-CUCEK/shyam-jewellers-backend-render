// GetPurityResponseDTO.java
package com.shyam.dto.response;

import com.shyam.entity.Purity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPurityResponseDTO {
  private Long purityId;
  private Long materialTypeId;
  private String materialTypeName;
  private String purityName;
  private BigDecimal purityFactor;
  private Boolean status;
  private String createdBy;
  private LocalDateTime createdAt;
  private String updatedBy;
  private LocalDateTime updatedAt;

  public static GetPurityResponseDTO fromEntity(Purity purity) {
    return GetPurityResponseDTO.builder()
        .purityId(purity.getPurityId())
        .materialTypeId(purity.getMaterialType().getMaterialTypeId())
        .materialTypeName(purity.getMaterialType().getName())
        .purityName(purity.getPurityName())
        .purityFactor(purity.getPurityFactor())
        .status(purity.getStatus())
        .createdBy(purity.getCreatedBy())
        .createdAt(purity.getCreatedAt())
        .updatedBy(purity.getUpdatedBy())
        .updatedAt(purity.getUpdatedAt())
        .build();
  }
}
