package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.*;

import com.shyam.common.constants.Role;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.jwt.JwtUtil;
import com.shyam.common.redis.service.TokenBlacklistService;
import com.shyam.common.service.RefreshTokenService;
import com.shyam.common.util.MapperUtil;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dao.AdminDAO;
import com.shyam.dto.NotificationMessage;
import com.shyam.dto.NotificationType;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.entity.AdminUsers;
import com.shyam.entity.OfferPhoto;
import com.shyam.mapper.AdminMapper;
import com.shyam.mapper.UserMapper;
import com.shyam.publisher.NotificationPublisher;
import com.shyam.service.AdminService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImp implements AdminService {

  private static final Logger logger = LoggerFactory.getLogger(AdminServiceImp.class);
  private final AdminMapper adminMapper;
  private final MessageSourceUtil messageSourceUtil;
  private final AdminDAO adminDAO;
  private final UserMapper userMapper;
  private final TokenBlacklistService tokenBlacklistService;
  private final RefreshTokenService refreshTokenService;
  private final NotificationPublisher notificationPublisher;

  @Override
  @Transactional
  public LogInResponseDTO initiateLogin(String email) {
    logger.info("Processing login initiation for admin: {}", email);
    AdminUsers admin = adminDAO.findUserByEmail(email);
    var otp = generateOTP();
    admin.setOtp(otp);
    admin.setOtpGeneratedTime(LocalDateTime.now());
    adminDAO.save(admin);
    NotificationMessage message =
        new NotificationMessage(admin.getEmail(), null, otp, NotificationType.LOGIN);
    notificationPublisher.publish(message);
    return userMapper.mapToUserLogInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_LOGIN_SEND_OTP));
  }

  @Override
  public ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> verifyLoginOtp(
      String email, String otp) {
    logger.info("Processing OTP verification for admin login");
    var admin = adminDAO.findUserByEmail(email);

    if (admin.getOtpGeneratedTime() == null
        || admin.getOtpGeneratedTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
      throw new SYMException(
          HttpStatus.UNAUTHORIZED,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_OTP_EXPIRED,
          "OTP expired",
          "OTP expired for email: " + email);
    }

    if (!Objects.equals(admin.getOtp(), otp)) {
      throw new SYMException(
          HttpStatus.UNAUTHORIZED,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_INVALID_OTP,
          "Invalid OTP",
          "Invalid OTP for email: " + email);
    }

    // Clear OTP fields after successful verification
    admin.setOtp(null);
    admin.setOtpGeneratedTime(null);
    adminDAO.save(admin);

    // Generate tokens
    var accessToken = JwtUtil.generateAccessToken(email, admin.getRole().name());
    var refreshToken = JwtUtil.generateRefreshToken();
    refreshTokenService.store(email, admin.getRole().name(), refreshToken);

    VerifyAdminResponseDTO response =
        VerifyAdminResponseDTO.builder()
            .token(accessToken)
            .refreshToken(refreshToken)
            .message("Login successful")
            .build();

    ResponseCookie cookie =
        ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(Duration.ofDays(1))
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new BaseResponseDTO<>(response, null));
  }

  @Override
  public AdminLogoutResponseDTO logout(String accessToken, String refreshToken) {
    logger.info("Processing to logout the admin");
    long expiryInSeconds =
        (JwtUtil.getExpiry(accessToken).getTime() - System.currentTimeMillis()) / 1000;
    if (expiryInSeconds > 0) {
      logger.info("Blacklisting the token...");
      tokenBlacklistService.blacklistToken(accessToken, expiryInSeconds);
    }
    if (refreshToken != null) {
      logger.info("Deleting admin refresh token...");
      refreshTokenService.delete(JwtUtil.getUsername(accessToken), JwtUtil.getRole(accessToken));
    }

    return adminMapper.mapToAdminLogoutInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_LOG_OUT));
  }

  @Override
  @Transactional
  public EditAdminResponseDTO edit(EditAdminRequestDTO editAdminRequestDTO) {
    logger.info("Processing edit  ");
    var admin = adminDAO.findUserByEmail(editAdminRequestDTO.getEmail());
    admin.setName(editAdminRequestDTO.getName());
    admin.setPhoneNumber(editAdminRequestDTO.getPhoneNumber());
    admin.setImageUrl(editAdminRequestDTO.getImageUrl());
    adminDAO.save(admin);
    NotificationMessage message =
        new NotificationMessage(admin.getEmail(), null, null, NotificationType.UPDATE);
    notificationPublisher.publish(message);
    return adminMapper.mapToAdminEditInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_EDIT_ADMIN));
  }

  @Override
  @Transactional
  public RegisterResponseDTO registerAdmin(RegisterRequestDTO registerRequestDTO) {
    if (adminDAO.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
      throw new SYMException(
          HttpStatus.CONFLICT,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_EMAIL_ALREADY_EXISTS,
          "Email already registered!",
          "Attempted to register with existing email: " + registerRequestDTO.getEmail());
    }

    var newUser = new AdminUsers();
    newUser.setName(registerRequestDTO.getName());
    newUser.setEmail(registerRequestDTO.getEmail());
    newUser.setPhoneNumber(registerRequestDTO.getPhoneNumber());
    newUser.setRole(MapperUtil.parseRole("ADMIN"));
    adminDAO.save(newUser);
    NotificationMessage message =
        new NotificationMessage(
            registerRequestDTO.getEmail(), null, null, NotificationType.WELCOME);
    notificationPublisher.publish(message);
    return adminMapper.mapToRegisterAdminInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_REGISTER_ADMIN));
  }

  @Override
  @Transactional
  public EditPhotoResponseDTO offerUpdate(EditPhotoRequestDTO request) {

    logger.info("========================================");
    logger.info("Processing to save offer section");
    logger.info("Position    : {}", request.getPosition());
    logger.info("Available   : {}", request.getIsAvailable());
    logger.info("Image URL   : {}", request.getImgUrl());
    logger.info("========================================");

    if (request.getPosition() == null) {
      throw new IllegalArgumentException("Offer position is required");
    }

    if (request.getPosition() < 1 || request.getPosition() > 5) {
      throw new IllegalArgumentException("Offer position must be between 1 and 5");
    }

    OfferPhoto offer = adminDAO.getPhotoByPosition(request.getPosition());

    LocalDateTime now = LocalDateTime.now();

    if (offer == null) {

      logger.info("No offer found at position {}. Creating new offer.", request.getPosition());

      offer = new OfferPhoto();

      offer.setPosition(request.getPosition());
      offer.setCreatedAt(now);

    } else {

      logger.info("Existing offer found. ID: {}. Updating offer.", offer.getId());
    }

    offer.setImgUrl(request.getImgUrl());
    offer.setIsAvailable(request.getIsAvailable());
    offer.setUpdatedAt(now);

    adminDAO.saveOffer(offer);

    logger.info("Offer saved successfully. Position: {}", request.getPosition());

    return adminMapper.mapToEditPhotoRequestDTOAdminInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_UPDATE_OFFER_ADMIN));
  }

  @Override
  @Transactional(readOnly = true)
  public List<GetOfferPhotoResponseDTO> getOfferPhoto() {
    logger.info("Getting offer photos");
    List<OfferPhoto> offerPhotos = adminDAO.getPhotosWithPosition();
    if (offerPhotos.isEmpty()) {
      logger.info("No available offer photo found in DB.");
      return Collections.emptyList();
    }
    return offerPhotos.stream()
        .map(
            offer ->
                GetOfferPhotoResponseDTO.builder()
                    .imgUrl(offer.getImgUrl())
                    .isAvailable(offer.getIsAvailable())
                    .position(offer.getPosition())
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public GetAdminListResponseDTO getAllAdmin() {

    logger.info("Processing request to get all admins");

    var roles = List.of(Role.ADMIN, Role.SUPER_ADMIN);

    var admins = adminDAO.findByRoleIn(roles);

    var responseDTOList =
        admins.stream().map(adminMapper::mapToGetAllAdminDTO).collect(Collectors.toList());

    return GetAdminListResponseDTO.builder().getAllAdminResponseDTOList(responseDTOList).build();
  }

  @Override
  @Transactional
  public DeleteAdminResponseDTO deleteAdmin(DeleteAdminRequestDTO deleteAdmin) {
    var admin = adminDAO.findUserByEmail(deleteAdmin.getEmail());
    adminDAO.delete(admin);
    return adminMapper.mapToDeleteAdminInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_DELETE_ADMIN));
  }

  @Override
  @Transactional(readOnly = true)
  public GetAdminResponseDTO getAdmin(GetAdminRequestDTO getAdminRequestDTO) {

    logger.info("Processing request to get admin");

    var admin = adminDAO.findByEmail(getAdminRequestDTO.getEmail());

    return adminMapper.mapToGetAdminDTO(admin.get());
  }

  private String generateOTP() {
    Random random = new Random();
    int otpValue = 100000 + random.nextInt(900000);
    return String.valueOf(otpValue);
  }
}
