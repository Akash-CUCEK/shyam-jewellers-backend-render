package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.*;

import com.shyam.common.email.EmailService;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.jwt.JwtUtil;
import com.shyam.common.redis.service.TokenBlacklistService;
import com.shyam.common.service.RefreshTokenService;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dao.AdminDAO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.entity.AdminUsers;
import com.shyam.mapper.AdminMapper;
import com.shyam.service.AdminService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImp implements AdminService {

  private static final Logger logger = LoggerFactory.getLogger(AdminServiceImp.class);
  private final AdminMapper adminMapper;
  private final MessageSourceUtil messageSourceUtil;
  private final AdminDAO adminDAO;
  private final BCryptPasswordEncoder passwordEncoder;
  private final TokenBlacklistService tokenBlacklistService;
  private final EmailService emailService;
  private final RefreshTokenService refreshTokenService;

  @Override
  public ResponseEntity<VerifyAdminResponseDTO> logIn(AdminLogInRequestDTO adminLogInRequestDTO) {

    AdminUsers admin = adminDAO.findUserByEmail(adminLogInRequestDTO.getEmail());
    if (!passwordEncoder.matches(adminLogInRequestDTO.getPassword(), admin.getPassword())) {
      throw new SYMException(
          HttpStatus.UNAUTHORIZED,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_PASSWORD_NOT_MATCHING,
          "Please enter correct password !",
          "Entered password is wrong, please enter correct password");
    }

    var role = admin.getRole().name();

    var accessToken = JwtUtil.generateAccessToken(admin.getEmail(), role);
    var refreshToken = JwtUtil.generateRefreshToken();

    var deviceId = adminLogInRequestDTO.getDeviceId();

    refreshTokenService.store(admin.getEmail(), role, refreshToken, deviceId);

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
        .body(
            VerifyAdminResponseDTO.builder()
                .message("Welcome Admin!")
                .token(accessToken)
                .refreshToken(refreshToken)
                .build());
  }

  @Override
  public ForgetPasswordResponseDTO forgetPassword(
      ForgetPasswordRequestDTO forgetPasswordRequestDTO) {
    var admin = adminDAO.findUserByEmail(forgetPasswordRequestDTO.getEmail());
    var otp = generateOTP();
    admin.setOtp(otp);
    admin.setOtpGeneratedTime(LocalDateTime.now());
    adminDAO.save(admin);
    sendVerificationEmail(forgetPasswordRequestDTO.getEmail(), otp);
    return adminMapper.mapToAdminForgetPasswordMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_FORGET_ADMIN_PASSWORD_SEND_OTP));
  }

  @Override
  public VerifyForgetPasswordResponseDTO forgetVerifyOtp(
      VerifyAdminRequestDTO verifyAdminRequestDTO) {
    logger.info("Processing the verify otp for password reset");
    var admin = adminDAO.findUserByEmail(verifyAdminRequestDTO.getEmail());
    if (admin.getOtpGeneratedTime() == null
        || admin.getOtpGeneratedTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
      throw new SYMException(
          HttpStatus.UNAUTHORIZED,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_OTP_EXPIRED,
          "OTP expired",
          "OTP expired for email: " + verifyAdminRequestDTO.getEmail());
    }
    if (!Objects.equals(verifyAdminRequestDTO.getOtp(), admin.getOtp())) {
      throw new SYMException(
          HttpStatus.UNAUTHORIZED,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_INVALID_OTP,
          "Invalid OTP",
          "Invalid OTP for email: " + verifyAdminRequestDTO.getEmail());
    }
    var password = passwordEncoder.encode(verifyAdminRequestDTO.getPassword());
    if (passwordEncoder.matches(admin.getPassword(), password)) {
      logger.info(
          "New password cannot be same as the old password! {} , {}",
          verifyAdminRequestDTO.getPassword(),
          password);
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_SAME_AS_OLD_PASSWORD,
          "New password cannot be same as the old password!",
          "Same password used while attempting to forgot password");
    }
    admin.setPassword(password);
    admin.setOtp(null);
    admin.setOtpGeneratedTime(null);
    adminDAO.save(admin);
    return adminMapper.mapToVerifyForgetOtpInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_FORGET_ADMIN_PASSWORD));
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
//    if (refreshToken != null) {
//      logger.info("Deleting the refresh token...");
//      refreshTokenService.delete(refreshToken);
//    }

    return adminMapper.mapToAdminLogoutInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_LOG_OUT));
  }

  @Override
  public EditAdminResponseDTO edit(EditAdminRequestDTO editAdminRequestDTO) {
    adminMapper.edit(editAdminRequestDTO);
    return adminMapper.mapToAdminEditInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_EDIT_ADMIN));
  }

  @Override
  public ChangePasswordResponseDTO changePassword(
      ChangePasswordRequestDTO changePasswordRequestDTO) {
    logger.info("Processing to change password");
    var adminUser = adminDAO.findUserByEmail(changePasswordRequestDTO.getEmail());
    if (!passwordEncoder.matches(changePasswordRequestDTO.getPassword(), adminUser.getPassword())) {
      logger.info("Incorrect password.");
      throw new SYMException(
          HttpStatus.UNAUTHORIZED,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_PASSWORD_NOT_MATCHING,
          "Please enter correct password !",
          "Entered password is wrong, please enter correct password");
    }
    if (passwordEncoder.matches(
        changePasswordRequestDTO.getNewPassword(), adminUser.getPassword())) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_SAME_AS_OLD_PASSWORD,
          "New password cannot be same as the old password!",
          "Same password used while attempting to change password");
    }
    adminUser.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
    adminDAO.save(adminUser);
    return adminMapper.mapToAdminChangePasswordInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_CHANGE_PASSWORD_ADMIN));
  }

  @Override
  public RegisterResponseDTO registerAdmin(RegisterRequestDTO registerRequestDTO) {
    adminMapper.registerAdmin(registerRequestDTO);
    return adminMapper.mapToRegisterAdminInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_REGISTER_ADMIN));
  }

  @Override
  public EditPhotoResponseDTO offerUpdate(EditPhotoRequestDTO editPhotoRequestDTO) {
    adminMapper.offerUpdate(editPhotoRequestDTO);
    return adminMapper.mapToEditPhotoRequestDTOAdminInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_UPDATE_OFFER_ADMIN));
  }

  @Override
  public GetOfferPhotoResponseDTO getOfferPhoto() {
    logger.info("Getting offer photos");
    var offer = adminDAO.getLatestOfferPhoto();
    if (offer == null) {
      logger.info("No offer photo found in DB.");
      return GetOfferPhotoResponseDTO.builder()
          .imgUrl1(null)
          .imgUrl2(null)
          .imgUrl3(null)
          .imgUrl4(null)
          .imgUrl5(null)
          .build();
    }
    return GetOfferPhotoResponseDTO.builder()
        .imgUrl1(offer.getImgUrl1())
        .imgUrl2(offer.getImgUrl2())
        .imgUrl3(offer.getImgUrl3())
        .imgUrl4(offer.getImgUrl4())
        .imgUrl5(offer.getImgUrl5())
        .build();
  }

  @Override
  public GetAdminListResponseDTO getAllAdmin() {
    return adminMapper.getAllAdmin();
  }

  @Override
  public DeleteAdminResponseDTO deleteAdmin(DeleteAdminRequestDTO deleteAdmin) {
    adminMapper.deleteAdmin(deleteAdmin);
    return adminMapper.mapToDeleteAdminInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_DELETE_ADMIN));
  }

  @Override
  public GetAdminResponseDTO getAdmin(GetAdminRequestDTO getAdminRequestDTO) {
    logger.info("Processing for getting admin ");
    return adminMapper.getAdmin(getAdminRequestDTO.getEmail());
  }

  private String generateOTP() {
    Random random = new Random();
    int otpValue = 100000 + random.nextInt(900000);
    return String.valueOf(otpValue);
  }

  private void sendVerificationEmail(String email, String otp) {
    var subject = "Shyam Jewellers Admin Login - OTP Verification";

    var body =
        "Dear Admin,\n\n"
            + "We received a request to sign in to your Shyam Jewellers Admin Account.\n\n"
            + "━━━━━━━━━━━━━━━━━━━━\n"
            + "🔐 Your One-Time Password (OTP)\n"
            + otp
            + "\n"
            + "━━━━━━━━━━━━━━━━━━━━\n\n"
            + "⏱ This OTP is valid for 5 minutes.\n"
            + "⚠ Please do not share this code with anyone.\n\n"
            + "If you did not initiate this request, please contact support immediately.\n\n"
            + "Regards,\n"
            + "Shyam Jewellers\n"
            + "Security Team";

    emailService.sendEmail(email, subject, body);
  }
}
