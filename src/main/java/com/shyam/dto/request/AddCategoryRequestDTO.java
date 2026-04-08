package com.shyam.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCategoryRequestDTO {
  private String name;
  private String createdBy;
  private Boolean status;
  private String imageUrl;
  private Boolean showOnHome;
  private String updatedBy;
}
