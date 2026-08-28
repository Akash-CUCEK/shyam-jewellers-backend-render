package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.*;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.jwt.JwtUtil;
import com.shyam.common.redis.service.TokenBlacklistService;
import com.shyam.common.service.RefreshTokenService;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dao.UserDAO;
import com.shyam.dto.NotificationMessage;
import com.shyam.dto.NotificationType;
import com.shyam.dto.request.OtpRequestDTO;
import com.shyam.dto.request.logInRequestDTO;
import com.shyam.dto.response.LogInResponseDTO;
import com.shyam.dto.response.LogoutResponseDTO;
import com.shyam.dto.response.OtpResponseDTO;
import com.shyam.entity.Users;
import com.shyam.mapper.UserMapper;
import com.shyam.publisher.NotificationPublisher;
import com.shyam.service.NotificationService;
import com.shyam.service.UserService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
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
public class UserServiceImp implements UserService {
  private final UserMapper userMapper;
  private final MessageSourceUtil messageSourceUtil;
  private final UserDAO userDAO;
  private final TokenBlacklistService tokenBlacklistService;
  private final RefreshTokenService refreshTokenService;
  private final NotificationService notificationService;
  private final NotificationPublisher notificationPublisher;

  @Override
  @Transactional
  public LogInResponseDTO logIn(logInRequestDTO logInRequestDTO) {
    log.info("Processing login for email: {}", logInRequestDTO.getEmail());
    var email = logInRequestDTO.getEmail();
    var otp = generateOTP();
    var otpGeneratedTime = LocalDateTime.now();
    var existingUserOpt = userDAO.findOnlyUser(email);
    Users user;
    if (existingUserOpt.isPresent()) {
      user = existingUserOpt.get();
      user.setOtp(otp);
      user.setOtpGeneratedTime(otpGeneratedTime);
    } else {
      user = Users.builder().email(email).otp(otp).otpGeneratedTime(otpGeneratedTime).build();
    }
    var savedUser = userDAO.save(user);
    var notificationMessage =
        new NotificationMessage(savedUser.getEmail(), null, otp, NotificationType.LOGIN);
    notificationPublisher.publish(notificationMessage);
    return userMapper.mapToUserLogInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_LOGIN_SEND_OTP));
  }

  @Override
  @Transactional
  public ResponseEntity<OtpResponseDTO> verify(OtpRequestDTO otpRequestDTO) {
    log.info("Processing for verifying the otp ");
    var user = userDAO.findUser(otpRequestDTO.getEmail());
    if (user.getOtpGeneratedTime() == null
        || user.getOtpGeneratedTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
      throw new SYMException(
          HttpStatus.UNAUTHORIZED,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_OTP_EXPIRED,
          "OTP expired",
          String.format("OTP expired for email: %s", otpRequestDTO.getEmail()));
    }
    if (!Objects.equals(otpRequestDTO.getOtp(), user.getOtp())) {
      throw new SYMException(
          HttpStatus.UNAUTHORIZED,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_INVALID_OTP,
          "Invalid OTP",
          "Invalid OTP for email: " + otpRequestDTO.getEmail());
    }
    var accessToken = JwtUtil.generateAccessToken(user.getEmail(), "USER");
    var refreshToken = JwtUtil.generateRefreshToken();
    refreshTokenService.store(user.getEmail(), "USER", refreshToken);

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
            OtpResponseDTO.builder()
                .message("Welcome to Shyam Jewellers!")
                .token(accessToken)
                .refreshToken(refreshToken)
                .build());
  }

  @Override
  @Transactional
  public LogoutResponseDTO logout(String accessToken, String refreshToken, String deviceId) {
    log.info("Processing to logout the user");
    long expiryInSeconds =
        (JwtUtil.getExpiry(accessToken).getTime() - System.currentTimeMillis()) / 1000;
    if (expiryInSeconds > 0) {
      log.info("Blacklisting the token...");
      tokenBlacklistService.blacklistToken(accessToken, expiryInSeconds);
    }
    if (refreshToken != null) {
      log.info("Deleting user refresh token...");
      refreshTokenService.delete(JwtUtil.getUsername(accessToken), "USER");
    }
    return userMapper.mapToUserLogoutInMessage(messageSourceUtil.getMessage(MESSAGE_CODE_LOG_OUT));
  }

  private String generateOTP() {
    Random random = new Random();
    int otpValue = 100000 + random.nextInt(900000);
    return String.valueOf(otpValue);
  }
}
