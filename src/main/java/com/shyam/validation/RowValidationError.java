package com.shyam.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RowValidationError {
  private int rowNumber;
  private String name;
  private String status;
  private String reason;
}
