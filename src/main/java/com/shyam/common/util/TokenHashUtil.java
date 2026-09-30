package com.shyam.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Refresh tokens are already high-entropy random strings, so they don't need
 * BCrypt's deliberate slowness (that's for low-entropy human passwords).
 * SHA-256 is fast and, crucially, deterministic - so the hash can be used
 * directly as a lookup key instead of loading a candidate row and comparing.
 */
public final class TokenHashUtil {

    private TokenHashUtil() {}

    public static String sha256Hex(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}