package com.shyam.controller;

import com.shyam.dto.JewelryPriceBreakdown;
import com.shyam.dto.request.JewelryPriceRequestDTO;
import com.shyam.service.JewelryPricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jewelry")
@RequiredArgsConstructor
@Tag(name = "Jewelry", description = "Jewelry price calculation endpoints")
public class JewelryController {

  private final JewelryPricingService jewelryPricingService;

  @Operation(summary = "Calculate jewelry price",
             description = "Calculates the final price of jewelry based on material type, net weight, and purity")
  @PostMapping("/calculate-price")
  public ResponseEntity<?> calculatePrice(@RequestBody JewelryPriceRequestDTO request) {
    try {
      JewelryPriceBreakdown priceBreakdown = jewelryPricingService.calculatePrice(
          request.getMaterialTypeId(),
          request.getNetWeight(),
          request.getPurity());

      return ResponseEntity.ok(priceBreakdown);
    } catch (IllegalArgumentException e) {
      // Handle cases like material type not found or inactive
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (IllegalStateException e) {
      // Handle case like no metal rate found
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      // Handle any other unexpected errors
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while calculating the price");
    }
  }
}