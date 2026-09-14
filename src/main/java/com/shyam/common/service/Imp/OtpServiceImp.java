package com.shyam.common.service.Imp;

import com.shyam.common.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Implementation of OtpService providing OTP generation and validation functionality.
 */
@Service
@Slf4j
public class OtpServiceImp implements OtpService {

    private final Random random = new Random();

    /**
     * Generates a random 6-digit OTP.
     *
     * @return a 6-digit numeric OTP as a string
     */
    @Override
    public String generateOTP() {
        int otpValue = 100000 + random.nextInt(900000);
        String otp = String.valueOf(otpValue);
        log.debug("Generated OTP: {}", otp);
        return otp;
    }

    /**
     * Validates if the provided OTP matches the stored OTP for the given user.
     *
     * @param storedOtp the OTP stored in the system
     * @param providedOtp the OTP provided by the user for validation
     * @return true if OTPs match, false otherwise
     */
    @Override
    public boolean validateOTP(String storedOtp, String providedOtp) {
        boolean isValid = storedOtp != null && storedOtp.equals(providedOtp);
        log.debug("OTP validation - Stored: {}, Provided: {}, Valid: {}",
                  storedOtp, providedOtp, isValid);
        return isValid;
    }
}