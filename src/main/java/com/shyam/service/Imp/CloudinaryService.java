package com.shyam.service.Imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
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
    this.cloudinary =
        new Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
  }

  public String upload(MultipartFile file) {
    try {
      // 🔐 validation
      if (!file.getContentType().startsWith("image/")) {
        throw new RuntimeException("Only image files allowed");
      }

      Map uploadResult =
          cloudinary
              .uploader()
              .upload(
                  file.getBytes(),
                  ObjectUtils.asMap(
                      "folder", "shyam-products", // 📁 optional folder
                      "resource_type", "image"));

      return uploadResult.get("secure_url").toString();

    } catch (Exception e) {
      throw new RuntimeException("Image upload failed: " + e.getMessage());
    }
  }
}
