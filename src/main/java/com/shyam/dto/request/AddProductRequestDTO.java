package com.shyam.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Hallmark certified is required")
    private Boolean hallmarkCertified;

    @NotNull(message = "Certification number is required")
    private String certificationNumber;

    @NotNull(message = "Discount type is required")
    private String discountType;

    @NotNull(message = "Discount value is required")
    private BigDecimal discountValue;

    @NotNull(message = "Discount valid till is required")
    private java.time.LocalDateTime discountValidTill;

    @NotBlank(message = "Created by is required")
    private String createdBy;
}