// package com.shyam.service.Imp;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.*;
//
// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.HttpCookies;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.ResponseCookie;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// import com.shyam.common.email.EmailService;
// import com.shyam.common.jwt.JwtUtil;
// import com.shyam.common.redis.service.TokenBlacklistService;
// import com.shyam.common.service.RefreshTokenService;
// import com.shyam.common.util.MessageSourceUtil;
// import com.shyam.dao.UserDAO;
// import com.shyam.dto.request.OtpRequestDTO;
// import com.shyam.dto.request.logInRequestDTO;
// import com.shyam.dto.response.LogInResponseDTO;
// import com.shyam.dto.response.OtpResponseDTO;
// import com.shyam.dto.response.LogoutResponseDTO;
// import com.shyam.entity.User;
// import com.shyam.mapper.UserMapper;
// import com.shyam.constants.MessageConstant;
// import com.shyam.constants.ErrorCodeConstants;
// import com.shyam.common.exception.domain.SYMErrorType;
// import com.shyam.common.exception.domain.SYMException;
//
// @ExtendWith(MockitoExtension.class)
// class UserServiceImpTest {
//
//    @Mock
//    private UserDAO userDAO;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @Mock
//    private EmailService emailService;
//
//    @Mock
//    private TokenBlacklistService tokenBlacklistService;
//
//    @Mock
//    private RefreshTokenService refreshTokenService;
//
//    @Mock
//    private MessageSourceUtil messageSourceUtil;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserServiceImp userServiceImp;
//
//    private User testUser;
//    private String testEmail = "test@example.com";
//    private String testPassword = "password123";
//    private String testOtp = "123456";
//    private String testDeviceId = "test-device";
//
//    @BeforeEach
//    void setUp() {
//        testUser = User.builder()
//                .email(testEmail)
//                .password("encodedPassword")
//                .otp(testOtp)
//                .otpGeneratedTime(LocalDateTime.now())
//                .build();
//    }
//
//    @Test
//    void testLoginSuccess() {
//        // Arrange
//        logInRequestDTO loginRequest = new logInRequestDTO();
//        loginRequest.setEmail(testEmail);
//        loginRequest.setPassword(testPassword);
//
//        when(userDAO.findUser(testEmail)).thenReturn(testUser);
//        when(passwordEncoder.matches(testPassword, testUser.getPassword())).thenReturn(true);
//        when(messageSourceUtil.getMessage(MessageConstant.MESSAGE_CODE_LOGIN_SEND_OTP))
//                .thenReturn("Login OTP sent");
//
//        // Act
//        LogInResponseDTO response = userServiceImp.logIn(loginRequest);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("Login OTP sent", response.getMessage());
//        verify(userMapper).logInMapper(loginRequest);
//    }
//
//    @Test
//    void testLoginUserNotFound() {
//        // Arrange
//        logInRequestDTO loginRequest = new logInRequestDTO();
//        loginRequest.setEmail(testEmail);
//        loginRequest.setPassword(testPassword);
//
//        when(userDAO.findUser(testEmail)).thenReturn(null);
//
//        // Act & Assert
//        Exception exception = assertThrows(Exception.class, () -> {
//            userServiceImp.logIn(loginRequest);
//        });
//
//        assertTrue(exception instanceof com.shyam.common.exception.domain.SYMException);
//        verify(userDAO).findUser(testEmail);
//    }
//
//    @Test
//    void testVerifyOtpSuccess() {
//        // Arrange
//        OtpRequestDTO otpRequest = new OtpRequestDTO();
//        otpRequest.setEmail(testEmail);
//        otpRequest.setOtp(testOtp);
//        otpRequest.setDeviceId(testDeviceId);
//
//        when(userDAO.findUser(testEmail)).thenReturn(testUser);
//        when(testUser.getOtpGeneratedTime()).thenReturn(LocalDateTime.now());
//        when(testUser.getOtp()).thenReturn(testOtp);
//        when(JwtUtil.generateAccessToken(testEmail, "USER")).thenReturn("test-access-token");
//        when(JwtUtil.generateRefreshToken()).thenReturn("test-refresh-token");
//
//        ResponseCookie mockCookie = ResponseCookie.from("refreshToken", "test-refresh-token")
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("Strict")
//                .path("/")
//                .maxAge(Duration.ofDays(1))
//                .build();
//
//        // Mock the cookie building - we'll skip asserting on the exact cookie for simplicity
//        when(userServiceImp.resolveDeviceId(testDeviceId)).thenReturn(testDeviceId);
//
//        // Act
//        ResponseEntity<OtpResponseDTO> responseEntity = userServiceImp.verify(otpRequest);
//
//        // Assert
//        assertNotNull(responseEntity);
//        assertEquals(200, responseEntity.getStatusCodeValue());
//        OtpResponseDTO response = responseEntity.getBody();
//        assertNotNull(response);
//        assertEquals("Welcome to Shyam Jewellers!", response.getMessage());
//        assertEquals("test-access-token", response.getToken());
//        assertEquals("test-refresh-token", response.getRefreshToken());
//
//        // Verify refresh token was stored
//        verify(refreshTokenService).store(testEmail, "USER", "test-refresh-token", testDeviceId);
//    }
//
//    @Test
//    void testVerifyOtpExpired() {
//        // Arrange
//        OtpRequestDTO otpRequest = new OtpRequestDTO();
//        otpRequest.setEmail(testEmail);
//        otpRequest.setOtp(testOtp);
//        otpRequest.setDeviceId(testDeviceId);
//
//        when(userDAO.findUser(testEmail)).thenReturn(testUser);
//        // Set OTP generated time to be more than 5 minutes ago
//        when(testUser.getOtpGeneratedTime()).thenReturn(LocalDateTime.now().minusMinutes(10));
//
//        // Act & Assert
//        Exception exception = assertThrows(Exception.class, () -> {
//            userServiceImp.verify(otpRequest);
//        });
//
//        assertTrue(exception instanceof com.shyam.common.exception.domain.SYMException);
//        assertEquals(ErrorCodeConstants.ERROR_CODE_AUTHZ_OTP_EXPIRED,
//                ((com.shyam.common.exception.domain.SYMException) exception).getErrorCode());
//    }
//
//    @Test
//    void testVerifyOtpInvalid() {
//        // Arrange
//        OtpRequestDTO otpRequest = new OtpRequestDTO();
//        otpRequest.setEmail(testEmail);
//        otpRequest.setOtp("wrong-otp"); // Wrong OTP
//        otpRequest.setDeviceId(testDeviceId);
//
//        when(userDAO.findUser(testEmail)).thenReturn(testUser);
//        when(testUser.getOtpGeneratedTime()).thenReturn(LocalDateTime.now());
//        when(testUser.getOtp()).thenReturn(testOtp); // Correct OTP in DB
//
//        // Act & Assert
//        Exception exception = assertThrows(Exception.class, () -> {
//            userServiceImp.verify(otpRequest);
//        });
//
//        assertTrue(exception instanceof com.shyam.common.exception.domain.SYMException);
//        assertEquals(ErrorCodeConstants.ERROR_CODE_AUTHZ_INVALID_OTP,
//                ((com.shyam.common.exception.domain.SYMException) exception).getErrorCode());
//    }
//
//    @Test
//    void testLogoutSuccess() {
//        // Arrange
//        String accessToken = "test-access-token";
//        String refreshToken = "test-refresh-token";
//
//
// when(JwtUtil.getExpiry(accessToken)).thenReturn(java.util.Date.from(LocalDateTime.now().plusHours(1).atZone(java.time.ZoneId.systemDefault()).toInstant()));
//        when(JwtUtil.getUsername(accessToken)).thenReturn(testEmail);
//        when(JwtUtil.getRole(accessToken)).thenReturn("USER");
//
//        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("Strict")
//                .path("/")
//                .maxAge(0)
//                .build();
//
//        // Act
//        LogoutResponseDTO response = userServiceImp.logout(accessToken, refreshToken,
// testDeviceId);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("Logout successful", response.getMessage()); // Assuming this is the message
// from MESSAGE_CODE_LOG_OUT
//
//        // Verify token blacklisting
//        verify(tokenBlacklistService).blacklistToken(accessToken, anyLong());
//        // Verify refresh token deletion
//        verify(refreshTokenService).delete(testEmail, "USER", testDeviceId);
//        // Verify user mapper call
//        verify(userMapper).mapToUserLogoutInMessage(anyString());
//    }
// }
