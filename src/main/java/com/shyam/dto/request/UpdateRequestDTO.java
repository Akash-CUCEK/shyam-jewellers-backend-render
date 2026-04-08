package com.shyam.dto.request;

import java.math.BigDecimal;

import com.shyam.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRequestDTO {
  private String name;
  private Long categoryId;
  private BigDecimal price;
  private Integer discountPercentage;
  private BigDecimal weight;
  private String materialType;
  private String shortDescription;
  private String fullDescription;
  private String gender;
  private Boolean isAvailable;
  private Integer quantity;
  private Integer availableStock;
  private String imageUrl;
  private String updatedBy;
}