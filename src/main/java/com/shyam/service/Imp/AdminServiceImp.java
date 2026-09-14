package com.shyam.service.Imp;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.AdminService;
import com.shyam.service.AdminManagementService;
import com.shyam.service.AuthService;
import com.shyam.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImp implements AdminService {

  private final AuthService authService;
  private final AdminManagementService adminManagementService;
  private final OfferService offerService;

  @Override
  @Transactional
  public LogInResponseDTO initiateLogin(String email) {
    return authService.initiateLogin(email);
  }

  @Override
  public ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> verifyLoginOtp(
      String email, String otp) {
    return authService.verifyLoginOtp(email, otp);
  }

  @Override
  public AdminLogoutResponseDTO logout(String accessToken, String refreshToken) {
    return authService.logout(accessToken, refreshToken);
  }

  @Override
  @Transactional
  public EditAdminResponseDTO edit(EditAdminRequestDTO editAdminRequestDTO) {
    return adminManagementService.edit(editAdminRequestDTO);
  }

  @Override
  @Transactional
  public RegisterResponseDTO registerAdmin(RegisterRequestDTO registerRequestDTO) {
    return adminManagementService.registerAdmin(registerRequestDTO);
  }

  @Override
  @Transactional
  public EditPhotoResponseDTO offerUpdate(EditPhotoRequestDTO request) {
    return offerService.offerUpdate(request);
  }

  @Override
  @Transactional(readOnly = true)
  public List<GetOfferPhotoResponseDTO> getOfferPhoto() {
    return offerService.getOfferPhoto();
  }

  @Override
  @Transactional(readOnly = true)
  public GetAdminListResponseDTO getAllAdmin() {
    return adminManagementService.getAllAdmin();
  }

  @Override
  @Transactional
  public DeleteAdminResponseDTO deleteAdmin(DeleteAdminRequestDTO deleteAdmin) {
    return adminManagementService.deleteAdmin(deleteAdmin);
  }

  @Override
  @Transactional(readOnly = true)
  public GetAdminResponseDTO getAdmin(GetAdminRequestDTO getAdminRequestDTO) {
    return adminManagementService.getAdmin(getAdminRequestDTO);
  }

  }