package com.shyam.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO implements Serializable {
  private Long id;
  private String name;
  private String category;
  private String stock;
  private BigDecimal price;
  private Integer discountPercentage;
  private BigDecimal weight;
  private String materialType;
  private String skuCode;
  private Integer availableStock;
  private String shortDescription;
  private String fullDescription;
  private String gender;
  private Double averageRating;
  private Boolean isAvailable;
  private String imageUrl;
}
