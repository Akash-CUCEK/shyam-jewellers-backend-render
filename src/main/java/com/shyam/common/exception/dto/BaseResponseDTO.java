package com.shyam.common.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponseDTO<T> {
  private T response;
  private ErrorResponseDTO errors;
}
