package com.shyam.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCategoriesResponseDTO {
  private Long categoryId;
  private String name;
  private Boolean showOnHome;
  private LocalDateTime createdAt;
  private String createdBy;
  private LocalDateTime updatedAt;
  private String updatedBy;
  private Boolean status;
}
