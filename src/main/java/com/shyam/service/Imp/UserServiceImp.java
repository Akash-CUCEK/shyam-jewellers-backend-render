package com.shyam.service.Imp;

import com.shyam.common.email.EmailService;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.jwt.JwtUtil;
import com.shyam.common.redis.service.TokenBlacklistService;
import com.shyam.common.service.RefreshTokenService;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dao.UserDAO;
import com.shyam.dto.request.OtpRequestDTO;
import com.shyam.dto.request.logInRequestDTO;
import com.shyam.dto.response.LogInResponseDTO;
import com.shyam.dto.response.LogoutResponseDTO;
import com.shyam.dto.response.OtpResponseDTO;
import com.shyam.mapper.UserMapper;
import com.shyam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.shyam.constants.MessageConstant.*;


@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private final UserMapper userMapper;
    private final MessageSourceUtil messageSourceUtil;
    private final UserDAO userDAO;
    private final EmailService emailService;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;


    @Override
    public LogInResponseDTO logIn(logInRequestDTO logInRequestDTO) {
        logger.info("Processing for logIn ");
        userMapper.logInMapper(logInRequestDTO);
        return userMapper.mapToUserLogInMessage(messageSourceUtil
                .getMessage(MESSAGE_CODE_LOGIN_SEND_OTP));
    }

    @Override
    public ResponseEntity<OtpResponseDTO> verify(OtpRequestDTO otpRequestDTO) {
        logger.info("Processing for verifying the otp ");
        var user = userDAO.findUser(otpRequestDTO.getEmail());
        if (user.getOtpGeneratedTime() == null ||
                user.getOtpGeneratedTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
                throw new SYMException(
                        HttpStatus.UNAUTHORIZED,
                        SYMErrorType.GENERIC_EXCEPTION,
                        ErrorCodeConstants.ERROR_CODE_AUTHZ_OTP_EXPIRED,
                        "OTP expired",
                        String.format("OTP expired for email: %s", otpRequestDTO.getEmail())
                );
        }
        if (!Objects.equals(otpRequestDTO.getOtp(), user.getOtp())) {
            throw new SYMException(
                    HttpStatus.UNAUTHORIZED,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_AUTHZ_INVALID_OTP,
                    "Invalid OTP",
                    "Invalid OTP for email: " + otpRequestDTO.getEmail()
            );
        }
        var accessToken = JwtUtil.generateAccessToken(user.getEmail(), "USER");
        var refreshToken = JwtUtil.generateRefreshToken();
        refreshTokenService.store(user.getEmail(), "USER", refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
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
                                .build()
                );
    }

    @Override
    public LogoutResponseDTO logout(String accessToken, String refreshToken) {
        logger.info("Processing request for log out");
        long expiryInSeconds =
                (JwtUtil.getExpiry(accessToken).getTime() - System.currentTimeMillis()) / 1000;

        if (expiryInSeconds > 0) {
            logger.info("Black listing token");
            tokenBlacklistService.blacklistToken(accessToken, expiryInSeconds);
        }

        if (refreshToken != null) {
            logger.info("Deleting refresh token");
            refreshTokenService.deleteByRefreshToken(refreshToken);
        }

        return userMapper.mapToUserLogoutInMessage(
                messageSourceUtil.getMessage(MESSAGE_CODE_LOG_OUT)
        );
    }

}
