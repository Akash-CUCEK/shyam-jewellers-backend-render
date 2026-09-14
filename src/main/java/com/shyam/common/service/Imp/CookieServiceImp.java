package com.shyam.common.service.Imp;

import com.shyam.common.service.CookieService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/** Implementation of CookieService providing cookie creation and manipulation functionality. */
@Service
@Slf4j
public class CookieServiceImp implements CookieService {

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
  @Override
  public ResponseCookie createResponseCookie(
      String name,
      String value,
      boolean httpOnly,
      boolean secure,
      String sameSite,
      String path,
      int maxAge) {
    ResponseCookie.ResponseCookieBuilder builder =
        ResponseCookie.from(name, value).httpOnly(httpOnly).path(path).maxAge(maxAge);

    if (secure) {
      builder.secure(true);
    }

    if (sameSite != null && !sameSite.isEmpty()) {
      builder.sameSite(sameSite);
    }

    ResponseCookie cookie = builder.build();
    log.debug(
        "Created ResponseCookie - Name: {}, Value: {}, HttpOnly: {}, Secure: {}, SameSite: {}, Path: {}, MaxAge: {}",
        name,
        value,
        httpOnly,
        secure,
        sameSite,
        path,
        maxAge);
    return cookie;
  }

  /**
   * Creates a ResponseCookie for setting a cookie with default secure settings.
   *
   * @param name the cookie name
   * @param value the cookie value
   * @param maxAge the maximum age in seconds (0 or negative to delete)
   * @return ResponseCookie with secure defaults
   */
  @Override
  public ResponseCookie createSecureCookie(String name, String value, int maxAge) {
    return createResponseCookie(name, value, true, true, "Strict", "/", maxAge);
  }

  /**
   * Creates a ResponseCookie for deleting a cookie.
   *
   * @param name the cookie name
   * @param path the cookie path
   * @return ResponseCookie configured to delete the cookie
   */
  @Override
  public ResponseCookie deleteCookie(String name, String path) {
    return createResponseCookie(name, "", true, true, "Strict", path, 0);
  }

  /**
   * Creates a jakarta.servlet.http.Cookie for backward compatibility.
   *
   * @param name the cookie name
   * @param value the cookie value
   * @param maxAge the maximum age in seconds
   * @return Cookie configured with the provided parameters
   */
  @Override
  public Cookie createCookie(String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setMaxAge(maxAge);
    log.debug("Created Cookie - Name: {}, Value: {}, MaxAge: {}", name, value, maxAge);
    return cookie;
  }
}
