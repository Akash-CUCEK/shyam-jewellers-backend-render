package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantRequestDTO {

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    @NotBlank(message = "Size is required")
    private String size;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity;
}