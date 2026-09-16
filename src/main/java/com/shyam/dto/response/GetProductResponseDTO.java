package com.shyam.dto.response;

import com.shyam.entity.Category;
import com.shyam.entity.MaterialType;
import com.shyam.entity.Product;
import com.shyam.entity.Purity;
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
  private LocalDateTime createdAt;
  private String createdBy;
  private LocalDateTime updatedAt;
  private String updatedBy;

  public static GetProductResponseDTO fromEntity(Product product) {
    if (product == null) {
      return null;
    }
    Category category = product.getCategory();
    MaterialType materialType = product.getMaterialType();
    Purity purity = product.getPurity();

    return GetProductResponseDTO.builder()
        .productId(product.getProductId())
        .productName(product.getProductName())
        .categoryId(category != null ? category.getCategoryId() : null)
        .categoryName(category != null ? category.getName() : null)
        .materialTypeId(materialType != null ? materialType.getMaterialTypeId() : null)
        .materialTypeName(materialType != null ? materialType.getName() : null)
        .purityId(purity != null ? purity.getPurityId() : null)
        .purityName(purity != null ? purity.getPurityName() : null)
        .purityFactor(purity != null ? purity.getPurityFactor() : null)
        .makingChargeType(product.getMakingChargeType())
        .makingChargeValue(product.getMakingChargeValue())
        .description(product.getDescription())
        .status(product.getStatus().name())
        .hallmarkCertified(product.getHallmarkCertified())
        .certificationNumber(product.getCertificationNumber())
        .discountType(product.getDiscountType())
        .discountValue(product.getDiscountValue())
        .discountValidTill(product.getDiscountValidTill())
        .createdAt(product.getCreatedAt())
        .createdBy(product.getCreatedBy())
        .updatedAt(product.getUpdatedAt())
        .updatedBy(product.getUpdatedBy())
        .build();
  }
}
