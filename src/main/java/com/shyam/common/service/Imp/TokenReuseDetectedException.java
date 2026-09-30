package com.shyam.common.service.Imp;

/**
 * Thrown when a refresh token that was already rotated once before gets
 * used again. This is the classic signal of a stolen token being replayed -
 * by the time this fires, RefreshTokenService.validate() has already
 * revoked every session this user has.
 */
public class TokenReuseDetectedException extends RuntimeException {
    public TokenReuseDetectedException(String email, String role) {
        super("Refresh token reuse detected for " + email + " (" + role + ")");
    }
}