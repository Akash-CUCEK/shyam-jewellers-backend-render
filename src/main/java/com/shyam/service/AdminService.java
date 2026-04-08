package com.shyam.service;

import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import org.springframework.http.ResponseEntity;

public interface AdminService {
  ResponseEntity<VerifyAdminResponseDTO> logIn(AdminLogInRequestDTO adminLogInRequestDTO);

  ForgetPasswordResponseDTO forgetPassword(ForgetPasswordRequestDTO forgetPasswordRequestDTO);

  VerifyForgetPasswordResponseDTO forgetVerifyOtp(VerifyAdminRequestDTO verifyAdminRequestDTO);

  AdminLogoutResponseDTO logout(String accessToken, String refreshToken);

  EditAdminResponseDTO edit(EditAdminRequestDTO editAdminRequestDTO);

  ChangePasswordResponseDTO changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

  RegisterResponseDTO registerAdmin(RegisterRequestDTO registerRequestDTO);

  EditPhotoResponseDTO offerUpdate(EditPhotoRequestDTO editPhotoRequestDTO);

  GetOfferPhotoResponseDTO getOfferPhoto();

  GetAdminListResponseDTO getAllAdmin();

  DeleteAdminResponseDTO deleteAdmin(DeleteAdminRequestDTO deleteAdmin);

  GetAdminResponseDTO getAdmin(GetAdminRequestDTO getAdminRequestDTO);
}
