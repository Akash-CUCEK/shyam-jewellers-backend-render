package com.shyam.mapper;

import com.shyam.dto.response.AddProductResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
  public AddProductResponseDTO mapToProduct(String message) {
    return AddProductResponseDTO.builder().message(message).build();
  }
}
