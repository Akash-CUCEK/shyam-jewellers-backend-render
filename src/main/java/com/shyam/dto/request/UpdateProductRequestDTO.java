package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequestDTO {

  @NotNull
  private Long productId;

  @NotNull
  private Long categoryId;

  @NotNull
  private Long materialTypeId;

  @NotNull
  private Long purityId;

  @NotBlank(message = "Making charge type is required")
  private String makingChargeType;

  @NotNull
  private BigDecimal makingChargeValue;

  private String description;

  @NotBlank(message = "Status is required")
  private String status;

  private Boolean hallmarkCertified;

  private String certificationNumber;

  private String discountType;

  private BigDecimal discountValue;

  private LocalDateTime discountValidTill;

  private String updatedBy;
}