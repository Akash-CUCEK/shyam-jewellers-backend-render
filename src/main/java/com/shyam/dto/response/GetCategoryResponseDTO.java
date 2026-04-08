package com.shyam.dto.response;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class GetCategoryResponseDTO implements Serializable {
  List<GetCategoriesResponseDTO> getCategoriesResponseDTO;
}
