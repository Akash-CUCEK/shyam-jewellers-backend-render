package com.shyam.validation;

import com.shyam.dao.CategoryDAO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryExcelValidation {

  private final CategoryDAO categoryDAO;

  public List<RowValidationError> validateExcel(Sheet sheet) {
    DataFormatter formatter = new DataFormatter();
    List<RowValidationError> errors = new ArrayList<>();

    // Regex: allow only letters, digits, spaces
    String namePattern = "^[a-zA-Z0-9 ]+$";

    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row == null) continue;

      String name = formatter.formatCellValue(row.getCell(0)).trim();
      String status = formatter.formatCellValue(row.getCell(1)).trim();

      StringBuilder reason = new StringBuilder();

      // Blank check
      if (name.isEmpty()) {
        reason.append("Name is blank; ");
      }

      // Special character check
      if (!name.isEmpty() && !name.matches(namePattern)) {
        reason.append("Name contains special characters; ");
      }

      // Duplicate check
      if (!name.isEmpty()
          && categoryDAO != null
          && !categoryDAO.isNameAvailable(name.trim().toLowerCase())) {
        reason.append("Duplicate category name; ");
      }

      // Status check
      if (status.isEmpty()) {
        reason.append("Status is blank; ");
      } else if (!status.equalsIgnoreCase("active") && !status.equalsIgnoreCase("inactive")) {
        reason.append("Status must be 'active' or 'inactive'; ");
      }

      // If any validation failed, add to error list
      if (reason.length() > 0) {
        errors.add(new RowValidationError(i, name, status, reason.toString().trim()));
      }
    }

    return errors;
  }
}
