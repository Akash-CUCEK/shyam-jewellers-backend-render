package com.shyam.mapper;

import com.shyam.dto.response.AddPurityResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PurityMapper {

  public AddPurityResponseDTO mapToPurity(String message) {
    return AddPurityResponseDTO.builder().response(message).build();
  }
}
