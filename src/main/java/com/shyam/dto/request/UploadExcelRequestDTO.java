package com.shyam.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadExcelRequestDTO {
  private MultipartFile file;
  private String createdBy;
}
