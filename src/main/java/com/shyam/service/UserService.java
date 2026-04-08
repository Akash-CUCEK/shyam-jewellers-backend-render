package com.shyam.service;

import com.shyam.dto.request.OtpRequestDTO;
import com.shyam.dto.request.logInRequestDTO;
import com.shyam.dto.response.LogInResponseDTO;
import com.shyam.dto.response.LogoutResponseDTO;
import com.shyam.dto.response.OtpResponseDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {
  LogInResponseDTO logIn(logInRequestDTO logInRequestDTO);

  ResponseEntity<OtpResponseDTO> verify(OtpRequestDTO otpRequestDTO);

//  LogoutResponseDTO logout(String authorization, String refreshToken);
}
