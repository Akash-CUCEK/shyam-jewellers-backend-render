package com.shyam.dto.response;

import com.shyam.entity.Category;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllProductsResponseDTO {
  private Long productIds;
  private String name;
  private Category category;
  private BigDecimal price;
  private BigDecimal weight;
  private String materialType;
  private Boolean isAvailable;
  private String imageUrl;
}
