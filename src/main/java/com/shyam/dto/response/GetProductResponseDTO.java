package com.shyam.dto.response;

import com.shyam.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetProductResponseDTO {

  private Long productId;
  private String productName;
  private Long categoryId;
  private String categoryName;
  private Long materialTypeId;
  private String materialTypeName;
  private Long purityId;
  private String purityName;
  private BigDecimal purityFactor;
  private String makingChargeType;
  private BigDecimal makingChargeValue;
  private String description;
  private String status;
  private Boolean hallmarkCertified;
  private String certificationNumber;
  private String discountType;
  private BigDecimal discountValue;
  private LocalDateTime discountValidTill;

  private String createdBy;
  private LocalDateTime createdAt;
  private String updatedBy;
  private LocalDateTime updatedAt;

  public static GetProductResponseDTO fromEntity(Product product) {
    return GetProductResponseDTO.builder()
        .productId(product.getProductId())
        .productName(product.getProductName())
        .categoryId(product.getCategory().getCategoryId())
        .categoryName(product.getCategory().getName())
        .materialTypeId(product.getMaterialType().getMaterialTypeId())
        .materialTypeName(product.getMaterialType().getName())
        .purityId(product.getPurity().getPurityId())
        .purityName(product.getPurity().getPurityName())
        .purityFactor(product.getPurity().getPurityFactor())
        .makingChargeType(product.getMakingChargeType())
        .makingChargeValue(product.getMakingChargeValue())
        .description(product.getDescription())
        .status(product.getStatus())
        .hallmarkCertified(product.getHallmarkCertified())
        .certificationNumber(product.getCertificationNumber())
        .discountType(product.getDiscountType())
        .discountValue(product.getDiscountValue())
        .discountValidTill(product.getDiscountValidTill())
        .createdBy(product.getCreatedBy())
        .createdAt(product.getCreatedAt())
        .updatedBy(product.getUpdatedBy())
        .updatedAt(product.getUpdatedAt())
        .build();
  }
}