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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Login initiated successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/initiateLogin")
  public BaseResponseDTO<LogInResponseDTO> initiateLogin(
      @Valid @RequestBody AdminLogInRequestDTO adminLogInRequestDTO) {
    log.debug("Entering initiateLogin method");
    log.info("Received request to initiate admin login for: {}", adminLogInRequestDTO.getEmail());

    var response = authService.initiateLogin(adminLogInRequestDTO.getEmail());
    log.debug("Exiting initiateLogin method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Verify admin login OTP",
      description = "Step 2: Verify OTP and complete login")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Login verified successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/verifyLoginOtp")
  public ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> verifyLoginOtp(
      @Valid @RequestBody VerifyAdminRequestDTO verifyAdminRequestDTO,
      @RequestHeader(value = "X-Client-Type", defaultValue = "WEB", required = false)
          String clientType) {
    log.debug("Entering verifyLoginOtp method");
    log.info(
        "Received request to verify admin login OTP for: {}", verifyAdminRequestDTO.getEmail());
    ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> response =
        authService.verifyLoginOtp(
            verifyAdminRequestDTO.getEmail(), verifyAdminRequestDTO.getOtp());

    log.debug("Exiting verifyLoginOtp method");
    return AuthResponseHelper.handleResponse(clientType, response, cookieService);
  }

  @Operation(summary = "Logout admin", description = "Logout admin and clear tokens.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Logout successful",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/logout")
  public ResponseEntity<BaseResponseDTO<AdminLogoutResponseDTO>> logout(
      @RequestHeader("Authorization") String authorization,
      @CookieValue(value = "refreshToken", required = false) String refreshTokenFromCookie,
      @Valid @RequestBody(required = false) LogoutRequestDTO logoutRequestDTO) {

    log.debug("Entering logout method");
    var accessToken = authorization.replace("Bearer ", "");

    String refreshToken =
        refreshTokenFromCookie != null
            ? refreshTokenFromCookie
            : (logoutRequestDTO != null ? logoutRequestDTO.getRefreshToken() : null);

    AdminLogoutResponseDTO response = authService.logout(accessToken, refreshToken);

    ResponseCookie deleteCookie = cookieService.deleteCookie("refreshToken", "/");

    log.debug("Exiting logout method");
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(new BaseResponseDTO<>(response, null));
  }

  @Operation(summary = "Edit admin", description = "Edit admin details.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Admin updated successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PostMapping(value = "/editAdmin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<EditAdminResponseDTO> edit(
      @Valid @RequestPart("admin") String adminJson,
      @RequestPart(value = "image", required = false) MultipartFile image)
      throws JsonProcessingException {

    log.debug("Entering edit method");
    log.info("Received request for edit");

    ObjectMapper mapper = new ObjectMapper();

    EditAdminRequestDTO editAdminRequestDTO =
        mapper.readValue(adminJson, EditAdminRequestDTO.class);

    if (image != null && !image.isEmpty()) {
      log.info("New profile image received. Uploading to Cloudinary...");

      String imageUrl = cloudinaryService.upload(image);
      log.debug("Image uploaded to Cloudinary, url: {}", imageUrl);

      editAdminRequestDTO.setImageUrl(imageUrl);

      log.info("New profile image uploaded successfully: {}", imageUrl);

    } else {

      log.info("No new profile image provided. Keeping existing image.");
    }
    var response = adminManagementService.edit(editAdminRequestDTO);
    log.debug("Exiting edit method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Register new Admin", description = "new admin register.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Admin registered successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/registerAdmin")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
  public BaseResponseDTO<RegisterResponseDTO> registerAdmin(
      @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
    log.debug("Entering registerAdmin method");
    log.info("Received request for register admin for");
    var response = adminManagementService.registerAdmin(registerRequestDTO);
    log.debug("Exiting registerAdmin method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Get Admin", description = "Get Admin.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/getAdminByEmail")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
  public BaseResponseDTO<GetAdminResponseDTO> getAllAdmin(
      @Valid @RequestBody GetAdminRequestDTO getAdminRequestDTO) {
    log.debug("Entering getAllAdmin method");
    log.info("Received request for getting admin ");
    var response = adminManagementService.getAdmin(getAdminRequestDTO);
    log.debug("Exiting getAllAdmin method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Get All Admin", description = "Get All Admin.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/getAllAdmin")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public BaseResponseDTO<GetAdminListResponseDTO> getAllAdmin() {

    log.debug("Entering getAllAdmin (list) method");
    log.info("Received request for getting all admin");
    var response = adminManagementService.getAllAdmin();
    log.debug("Exiting getAllAdmin (list) method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Delete Admin", description = "Delete Admin.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Admin deleted successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Admin not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/deleteAdmin")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public BaseResponseDTO<DeleteAdminResponseDTO> deleteAdmin(
      @Valid @RequestBody DeleteAdminRequestDTO deleteAdmin) {
    log.debug("Entering deleteAdmin method");
    log.info("Received request for delete admin");
    var response = adminManagementService.deleteAdmin(deleteAdmin);
    log.debug("Exiting deleteAdmin method");
    return new BaseResponseDTO<>(response, null);
  }
}
