package com.shyam.service.Imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class CloudinaryService {

  @Value("${cloudinary.cloud_name}")
  private String cloudName;

  @Value("${cloudinary.api_key}")
  private String apiKey;

  @Value("${cloudinary.api_secret}")
  private String apiSecret;

  private Cloudinary cloudinary;

  @PostConstruct
  public void init() {

    log.info("========================================");
    log.info("☁️ INITIALIZING CLOUDINARY");
    log.info("Cloud Name : {}", cloudName);
    log.info("API Key    : {}", apiKey != null ? "PRESENT" : "NULL");
    log.info(
            "API Secret : {}",
            apiSecret != null ? "PRESENT" : "NULL"
    );
    log.info("========================================");

    this.cloudinary = new Cloudinary(
            ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret
            )
    );

    log.info("✅ Cloudinary initialized successfully");
  }

  public String upload(MultipartFile file) {

    log.info("========================================");
    log.info("☁️ CLOUDINARY UPLOAD STARTED");
    log.info("========================================");

    try {

      // -----------------------------------------
      // NULL CHECK
      // -----------------------------------------

      if (file == null) {
        log.error("❌ MultipartFile is NULL");
        throw new RuntimeException("Image file is null");
      }

      // -----------------------------------------
      // EMPTY CHECK
      // -----------------------------------------

      if (file.isEmpty()) {
        log.error("❌ MultipartFile is EMPTY");
        throw new RuntimeException("Image file is empty");
      }

      // -----------------------------------------
      // FILE DETAILS
      // -----------------------------------------

      String fileName = file.getOriginalFilename();
      String contentType = file.getContentType();
      long fileSize = file.getSize();

      log.info("📄 File Name    : {}", fileName);
      log.info("📄 Content Type : {}", contentType);
      log.info("📄 File Size    : {} bytes", fileSize);

      // -----------------------------------------
      // CONTENT TYPE VALIDATION
      // -----------------------------------------

      if (contentType == null || !contentType.startsWith("image/")) {

        log.error(
                "❌ Invalid content type: {}",
                contentType
        );

        throw new RuntimeException(
                "Only image files are allowed"
        );
      }

      // -----------------------------------------
      // CLOUDINARY CHECK
      // -----------------------------------------

      if (cloudinary == null) {
        log.error("❌ Cloudinary instance is NULL");
        throw new RuntimeException(
                "Cloudinary is not initialized"
        );
      }

      // -----------------------------------------
      // UPLOAD
      // -----------------------------------------

      log.info("☁️ Uploading image to Cloudinary...");

      Map<String, Object> uploadResult =
              cloudinary
                      .uploader()
                      .upload(
                              file.getBytes(),
                              ObjectUtils.asMap(
                                      "folder", "shyam-products",
                                      "resource_type", "image"
                              )
                      );

      log.info("☁️ Cloudinary response received");

      // -----------------------------------------
      // GET URL
      // -----------------------------------------

      Object secureUrl = uploadResult.get("secure_url");

      if (secureUrl == null) {

        log.error(
                "❌ secure_url missing from Cloudinary response"
        );

        log.error(
                "Cloudinary response: {}",
                uploadResult
        );

        throw new RuntimeException(
                "Cloudinary did not return secure URL"
        );
      }

      String imageUrl = secureUrl.toString();

      log.info("========================================");
      log.info("✅ CLOUDINARY UPLOAD SUCCESS");
      log.info("IMAGE URL: {}", imageUrl);
      log.info("========================================");

      return imageUrl;

    } catch (Exception e) {

      log.error("========================================");
      log.error("❌ CLOUDINARY UPLOAD FAILED");
      log.error("ERROR TYPE: {}", e.getClass().getName());
      log.error("ERROR MESSAGE: {}", e.getMessage());
      log.error("========================================");

      // 🔥 VERY IMPORTANT
      // Full stacktrace print karega
      log.error("Cloudinary upload exception", e);

      throw new RuntimeException(
              "Image upload failed: " + e.getMessage(),
              e
      );
    }
  }
}