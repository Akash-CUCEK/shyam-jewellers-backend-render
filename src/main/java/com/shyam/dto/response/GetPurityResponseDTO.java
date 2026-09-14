package com.shyam.dto.response;

import com.shyam.entity.Purity;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPurityResponseDTO {

  private Long purityId;
  private String purityName;
  private BigDecimal purityFactor;

  private String createdBy;
  private LocalDateTime createdAt;
  private String updatedBy;
  private LocalDateTime updatedAt;
  private Boolean status;

  public static GetPurityResponseDTO fromEntity(Purity purity) {
    return GetPurityResponseDTO.builder()
        .purityId(purity.getPurityId())
        .purityName(purity.getPurityName())
        .purityFactor(purity.getPurityFactor())
        .createdBy(purity.getCreatedBy())
        .createdAt(purity.getCreatedAt())
        .updatedBy(purity.getUpdatedBy())
        .updatedAt(purity.getUpdatedAt())
        .status(purity.getStatus())
        .build();
  }
}