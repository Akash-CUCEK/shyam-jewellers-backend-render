package com.shyam.scheduler;

import com.shyam.service.MetalRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MetalRateFetchScheduler {

  private final MetalRateService metalRateService;

  public MetalRateFetchScheduler(MetalRateService metalRateService) {
    this.metalRateService = metalRateService;
  }

  /**
   * Fetches metal rates at 6 AM, 2 PM, and 10 PM every day.
   * Cron expression: "0 0 6,14,22 * * *"
   */
  @Scheduled(cron = "0 0 6,14,22 * * *")
  public void fetchMetalRates() {
    log.info("Starting scheduled metal rate fetch job");
    metalRateService.fetchAndStoreRates();
    log.info("Completed scheduled metal rate fetch job");
  }
}