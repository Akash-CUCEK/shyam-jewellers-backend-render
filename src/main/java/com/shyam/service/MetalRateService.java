package com.shyam.service;

import com.shyam.entity.MaterialType;
import com.shyam.entity.MetalRate;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.MetalRateRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MetalRateService {

  private final MetalRateRepository metalRateRepository;
  private final MaterialTypeRepository materialTypeRepository;
  private final RestTemplate restTemplate;

  @Value("${metals.dev.api.key}")
  private String apiKey;

  @Value("${metals.dev.api.url}")
  private String apiUrl;

  public MetalRateService(
      MetalRateRepository metalRateRepository,
      MaterialTypeRepository materialTypeRepository,
      RestTemplate restTemplate) {
    this.metalRateRepository = metalRateRepository;
    this.materialTypeRepository = materialTypeRepository;
    this.restTemplate = restTemplate;
  }

  /** Fetches metal rates from the external API and stores them for Gold and Silver. */
  public void fetchAndStoreRates() {
    try {
      // Build the API URL
      String url = apiUrl + "?api_key=" + apiKey + "&authority=ibja&currency=INR&unit=g";
      log.info("Fetching metal rates from: {}", url);

      // Make the HTTP request
      ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        Map<String, Object> responseBody = response.getBody();
        log.info("API response: {}", responseBody);

        // Extract the gold and silver rates
        Object goldRateObj = responseBody.get("ibja_gold");
        Object silverRateObj = responseBody.get("ibja_silver");

        if (goldRateObj != null) {
          processMetalRate("Gold", goldRateObj);
        } else {
          log.warn("Gold rate not found in API response");
        }

        if (silverRateObj != null) {
          processMetalRate("Silver", silverRateObj);
        } else {
          log.warn("Silver rate not found in API response");
        }
      } else {
        log.error(
            "Failed to fetch metal rates. Status code: {}, Body: {}",
            response.getStatusCode(),
            response.getBody());
      }
    } catch (Exception e) {
      log.error("Error occurred while fetching metal rates", e);
      // Do not rethrow to avoid stopping the scheduler
    }
  }

  /**
   * Processes the rate for a given metal type.
   *
   * @param materialTypeName the name of the material type (e.g., "Gold", "Silver")
   * @param rateObj the rate object from the API response
   */
  private void processMetalRate(String materialTypeName, Object rateObj) {
    try {
      // Convert the rate object to BigDecimal
      BigDecimal rate = null;
      if (rateObj instanceof Number) {
        rate = new BigDecimal(rateObj.toString());
      } else if (rateObj instanceof String) {
        rate = new BigDecimal((String) rateObj);
      } else {
        log.warn("Unable to parse rate for {}: {}", materialTypeName, rateObj);
        return;
      }

      // Find the material type by name (case-insensitive)
      MaterialType materialType = materialTypeRepository.findByNameIgnoreCase(materialTypeName);
      if (materialType == null) {
        log.warn("Material type not found for name: {} (case-insensitive)", materialTypeName);
        return;
      }

      if (!materialType.getStatus()) {
        log.warn(
            "Material type {} is inactive (status=false), skipping rate update", materialTypeName);
        return;
      }

      // Create and save the metal rate
      MetalRate metalRate =
          MetalRate.builder()
              .materialType(materialType)
              .ratePerGram(rate)
              .source("ibja")
              .fetchedAt(LocalDateTime.now())
              .build();

      metalRateRepository.save(metalRate);
      log.info("Successfully saved metal rate for {}: {} per gram", materialTypeName, rate);
    } catch (Exception e) {
      log.error("Error processing metal rate for {}", materialTypeName, e);
    }
  }
}
