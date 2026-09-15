package com.shyam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.service.CookieService;
import com.shyam.common.util.AuthResponseHelper;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.*;
import com.shyam.service.Imp.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

  private final AuthService authService;
  private final AdminManagementService adminManagementService;
  private final CloudinaryService cloudinaryService;
  private final CookieService cookieService;

  @Operation(summary = "Initiate admin login", description = "Step 1: Send OTP to admin email")
  @PostMapping("/initiateLogin")
  public BaseResponseDTO<LogInResponseDTO> initiateLogin(
      @RequestBody AdminLogInRequestDTO adminLogInRequestDTO) {

    log.info("Received request to initiate admin login for: {}", adminLogInRequestDTO.getEmail());

    var response = authService.initiateLogin(adminLogInRequestDTO.getEmail());

    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Verify admin login OTP",
      description = "Step 2: Verify OTP and complete login")
  @PostMapping("/verifyLoginOtp")
  public ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> verifyLoginOtp(
      @RequestBody VerifyAdminRequestDTO verifyAdminRequestDTO,
      @RequestHeader(value = "X-Client-Type", defaultValue = "WEB", required = false)
          String clientType) {
    log.info(
        "Received request to verify admin login OTP for: {}", verifyAdminRequestDTO.getEmail());
    ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> response =
        authService.verifyLoginOtp(
            verifyAdminRequestDTO.getEmail(), verifyAdminRequestDTO.getOtp());

    return AuthResponseHelper.handleResponse(clientType, response, cookieService);
  }

  @PostMapping("/logout")
  public ResponseEntity<BaseResponseDTO<AdminLogoutResponseDTO>> logout(
      @RequestHeader("Authorization") String authorization,
      @CookieValue(value = "refreshToken", required = false) String refreshTokenFromCookie,
      @RequestBody(required = false) LogoutRequestDTO logoutRequestDTO) {

    var accessToken = authorization.replace("Bearer ", "");

    String refreshToken =
        refreshTokenFromCookie != null
            ? refreshTokenFromCookie
            : (logoutRequestDTO != null ? logoutRequestDTO.getRefreshToken() : null);

    AdminLogoutResponseDTO response = authService.logout(accessToken, refreshToken);

    ResponseCookie deleteCookie = cookieService.deleteCookie("refreshToken", "/");

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(new BaseResponseDTO<>(response, null));
  }

  @Operation(summary = "Edit admin", description = "Edit admin details.")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PostMapping(value = "/editAdmin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<EditAdminResponseDTO> edit(
      @RequestPart("admin") String adminJson,
      @RequestPart(value = "image", required = false) MultipartFile image)
      throws JsonProcessingException {

    log.info("Received request for edit");

    ObjectMapper mapper = new ObjectMapper();

    EditAdminRequestDTO editAdminRequestDTO =
        mapper.readValue(adminJson, EditAdminRequestDTO.class);

    if (image != null && !image.isEmpty()) {
      log.info("New profile image received. Uploading to Cloudinary...");

      String imageUrl = cloudinaryService.upload(image);

      editAdminRequestDTO.setImageUrl(imageUrl);

      log.info("New profile image uploaded successfully: {}", imageUrl);

    } else {

      log.info("No new profile image provided. Keeping existing image.");
    }
    var response = adminManagementService.edit(editAdminRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Register new Admin", description = "new admin register.")
  @PostMapping("/registerAdmin")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
  public BaseResponseDTO<RegisterResponseDTO> registerAdmin(
      @RequestBody RegisterRequestDTO registerRequestDTO) {
    log.info("Received request for register admin for");
    var response = adminManagementService.registerAdmin(registerRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Get Admin", description = "Get Admin.")
  @PostMapping("/getAdminByEmail")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
  public BaseResponseDTO<GetAdminResponseDTO> getAllAdmin(
      @RequestBody GetAdminRequestDTO getAdminRequestDTO) {
    log.info("Received request for getting admin ");
    var response = adminManagementService.getAdmin(getAdminRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Get All Admin", description = "Get All Admin.")
  @PostMapping("/getAllAdmin")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public BaseResponseDTO<GetAdminListResponseDTO> getAllAdmin() {

    log.info("Received request for getting all admin");
    var response = adminManagementService.getAllAdmin();
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "delete Admin", description = "Delete Admin.")
  @PostMapping("/deleteAdmin")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public BaseResponseDTO<DeleteAdminResponseDTO> deleteAdmin(
      @RequestBody DeleteAdminRequestDTO deleteAdmin) {
    log.info("Received request for delete admin");
    var response = adminManagementService.deleteAdmin(deleteAdmin);
    return new BaseResponseDTO<>(response, null);
  }
}
