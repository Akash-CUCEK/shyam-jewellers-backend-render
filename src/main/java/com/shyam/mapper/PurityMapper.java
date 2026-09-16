package com.shyam.mapper;

import com.shyam.dto.response.AddPurityResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PurityMapper {
  public AddPurityResponseDTO mapToPurity(String message) {
    return AddPurityResponseDTO.builder().message(message).build();
  }
}
