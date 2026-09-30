package com.shyam.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequestDTO {

  private Long id;

  private String name;

  private String updatedBy;

  private Boolean status;

  private String imageUrl;

  private Boolean showOnHome;
}
