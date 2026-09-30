package com.shyam.mapper;

import com.shyam.dto.response.AddMaterialTypeResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialTypeMapper {

  public AddMaterialTypeResponseDTO mapToMaterialType(String message) {
    return AddMaterialTypeResponseDTO.builder().response(message).build();
  }
}
