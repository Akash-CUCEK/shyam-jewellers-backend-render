package com.shyam.service;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import org.springframework.http.ResponseEntity;

public interface AdminService {
  LogInResponseDTO initiateLogin(String email);

  ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> verifyLoginOtp(String email, String otp);

  AdminLogoutResponseDTO logout(String accessToken, String refreshToken, String deviceId);

  EditAdminResponseDTO edit(EditAdminRequestDTO editAdminRequestDTO);

  RegisterResponseDTO registerAdmin(RegisterRequestDTO registerRequestDTO);

  EditPhotoResponseDTO offerUpdate(EditPhotoRequestDTO editPhotoRequestDTO);

  GetOfferPhotoResponseDTO getOfferPhoto();

  GetAdminListResponseDTO getAllAdmin();

  DeleteAdminResponseDTO deleteAdmin(DeleteAdminRequestDTO deleteAdmin);

  GetAdminResponseDTO getAdmin(GetAdminRequestDTO getAdminRequestDTO);
}
