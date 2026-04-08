package com.shyam.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCategoryByIdResponseDTO implements Serializable {
  private Long categoryId;
  private String name;
  private LocalDateTime createdAt;
  private String createdBy;
  private LocalDateTime updatedAt;
  private String updatedBy;
  private Boolean status;
  private String imageUrl;
  private String showOnHome;
}
