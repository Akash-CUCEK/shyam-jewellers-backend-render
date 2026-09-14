package com.shyam.common.service;

import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

/**
 * Utility service for cookie creation and manipulation. Provides centralized cookie functionality
 * to eliminate code duplication.
 */
public interface CookieService {

  /**
   * Creates a ResponseCookie for setting a cookie in HTTP response.
   *
   * @param name the cookie name
   * @param value the cookie value
   * @param httpOnly whether the cookie should be HTTP only
   * @param secure whether the cookie should be secure
   * @param sameSite the SameSite attribute value
   * @param path the cookie path
   * @param maxAge the maximum age in seconds (0 or negative to delete)
   * @return ResponseCookie configured with the provided parameters
   */
  ResponseCookie createResponseCookie(
      String name,
      String value,
      boolean httpOnly,
      boolean secure,
      String sameSite,
      String path,
      int maxAge);

  /**
   * Creates a ResponseCookie for setting a cookie with default secure settings.
   *
   * @param name the cookie name
   * @param value the cookie value
   * @param maxAge the maximum age in seconds (0 or negative to delete)
   * @return ResponseCookie with secure defaults
   */
  ResponseCookie createSecureCookie(String name, String value, int maxAge);

  /**
   * Creates a ResponseCookie for deleting a cookie.
   *
   * @param name the cookie name
   * @param path the cookie path
   * @return ResponseCookie configured to delete the cookie
   */
  ResponseCookie deleteCookie(String name, String path);

  /**
   * Creates a jakarta.servlet.http.Cookie for backward compatibility.
   *
   * @param name the cookie name
   * @param value the cookie value
   * @param maxAge the maximum age in seconds (0 or negative to delete)
   * @return Cookie configured with the provided parameters
   */
  Cookie createCookie(String name, String value, int maxAge);
}
