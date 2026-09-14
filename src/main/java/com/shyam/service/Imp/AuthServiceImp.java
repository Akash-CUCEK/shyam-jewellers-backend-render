package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.MESSAGE_CODE_LOGIN_SEND_OTP;
import static com.shyam.constants.MessageConstant.MESSAGE_CODE_LOG_OUT;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.jwt.JwtUtil;
import com.shyam.common.service.OtpService;
import com.shyam.common.service.RefreshTokenService;
import com.shyam.common.service.TokenBlacklistService;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dao.AdminDAO;
import com.shyam.dto.NotificationMessage;
import com.shyam.dto.NotificationType;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.entity.AdminUsers;
import com.shyam.mapper.AdminMapper;
import com.shyam.mapper.UserMapper;
import com.shyam.service.AuthService;
import com.shyam.service.NotificationService;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImp implements AuthService {

  private final AdminMapper adminMapper;
  private final MessageSourceUtil messageSourceUtil;
  private final AdminDAO adminDAO;
  private final UserMapper userMapper;
  private final OtpService otpService;
  private final TokenBlacklistService tokenBlacklistService;
  private final RefreshTokenService refreshTokenService;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public LogInResponseDTO initiateLogin(String email) {
    log.info("Processing login initiation for admin: {}", email);
    AdminUsers admin = adminDAO.findUserByEmail(email);
    var otp = otpService.generateOTP();
    admin.setOtp(otp);
    admin.setOtpGeneratedTime(LocalDateTime.now());
    adminDAO.save(admin);
    NotificationMessage message =
        new NotificationMessage(admin.getEmail(), null, otp, NotificationType.LOGIN);
    notificationService.process(message);
    return userMapper.mapToUserLogInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_LOGIN_SEND_OTP));
  }

  @Override
  public ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> verifyLoginOtp(
      String email, String otp) {
    log.info("Processing OTP verification for admin login");
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

    if (!otpService.validateOTP(admin.getOtp(), otp)) {
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
    log.info("Processing to logout the admin");
    long expiryInSeconds =
        (JwtUtil.getExpiry(accessToken).getTime() - System.currentTimeMillis()) / 1000;
    if (expiryInSeconds > 0) {
      log.info("Blacklisting the token...");
      tokenBlacklistService.blacklistToken(accessToken, expiryInSeconds);
    }
    if (refreshToken != null) {
      log.info("Deleting admin refresh token...");
      refreshTokenService.delete(JwtUtil.getUsername(accessToken), JwtUtil.getRole(accessToken));
    }

    return adminMapper.mapToAdminLogoutInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_LOG_OUT));
  }
}
