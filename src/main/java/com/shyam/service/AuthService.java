package com.shyam.service;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for authentication-related operations. Follows Single Responsibility Principle
 * by handling only auth concerns.
 */
public interface AuthService {

  /**
   * Initiates the login process by sending OTP to the user's email.
   *
   * @param email the user's email address
   * @return login response containing OTP sending status
   */
  LogInResponseDTO initiateLogin(String email);

  /**
   * Verifies the OTP sent to the user's email or phone.
   *
   * @param email the user's email address
   * @param otp the OTP provided by the user
   * @return response entity containing login tokens and status
   */
  ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> verifyLoginOtp(String email, String otp);

  /**
   * Logs out the user by invalidating tokens and cleaning up sessions.
   *
   * @param accessToken the JWT access token to be blacklisted
   * @param refreshToken the refresh token to be removed
   * @return logout response indicating success
   */
  AdminLogoutResponseDTO logout(String accessToken, String refreshToken);
}
