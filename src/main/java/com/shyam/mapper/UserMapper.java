package com.shyam.mapper;

import com.shyam.dto.response.LogInResponseDTO;
import com.shyam.dto.response.LogoutResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
  private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

  public LogInResponseDTO mapToUserLogInMessage(String successMessage) {
    return LogInResponseDTO.builder().message(successMessage).build();
  }

  public LogoutResponseDTO mapToUserLogoutInMessage(String successMessage) {
    return LogoutResponseDTO.builder().message(successMessage).build();
  }
}
