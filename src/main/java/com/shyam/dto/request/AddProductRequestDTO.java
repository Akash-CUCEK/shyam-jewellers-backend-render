package com.shyam.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductRequestDTO {

  @NotNull(message = "Category ID is required")
  private Long categoryId;

  @NotNull(message = "Material type ID is required")
  private Long materialTypeId;

  @NotNull(message = "Purity ID is required")
  private Long purityId;

  @NotNull(message = "Making charge value is required")
  @Positive(message = "Making charge value must be positive")
  private BigDecimal makingChargeValue;

  private String description;

  @NotNull(message = "Hallmark certified is required")
  private Boolean hallmarkCertified;

  private String certificationNumber;

  private String discountType;

  private BigDecimal discountValue;

  private LocalDateTime discountValidTill;

  @NotBlank(message = "Created by is required")
  private String createdBy;

  @Valid
  private List<ProductVariantRequestDTO> variants;

  private List<Long> tagIds;

  private Integer primaryImageIndex;
}