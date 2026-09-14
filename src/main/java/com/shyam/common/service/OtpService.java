package com.shyam.common.service;

/**
 * Utility service for OTP (One-Time Password) generation and validation.
 * Provides centralized OTP functionality to eliminate code duplication.
 */
public interface OtpService {

    /**
     * Generates a random 6-digit OTP.
     *
     * @return a 6-digit numeric OTP as a string
     */
    String generateOTP();

    /**
     * Validates if the provided OTP matches the stored OTP for the given user.
     *
     * @param storedOtp the OTP stored in the system
     * @param providedOtp the OTP provided by the user for validation
     * @return true if OTPs match, false otherwise
     */
    boolean validateOTP(String storedOtp, String providedOtp);
}