package com.shyam.service;

import com.shyam.dto.JewelryPriceBreakdown;
import com.shyam.entity.MaterialType;
import com.shyam.entity.MetalRate;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.MetalRateRepository;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JewelryPricingService {

  private final MaterialTypeRepository materialTypeRepository;
  private final MetalRateRepository metalRateRepository;

  public JewelryPricingService(MaterialTypeRepository materialTypeRepository,
                               MetalRateRepository metalRateRepository) {
    this.materialTypeRepository = materialTypeRepository;
    this.metalRateRepository = metalRateRepository;
  }

  /**
   * Calculates the jewelry price breakdown based on the given inputs.
   *
   * @param materialTypeId the ID of the material type (e.g., Gold, Silver)
   * @param netWeight the net weight of the jewelry in grams
   * @param purity the purity of the metal as a fraction (e.g., 0.916 for 22K gold)
   * @return the price breakdown containing metalValue, makingCharge, gst, and finalPrice
   */
  public JewelryPriceBreakdown calculatePrice(Long materialTypeId, BigDecimal netWeight, BigDecimal purity) {
    // Fetch the material type
    MaterialType materialType = materialTypeRepository.findById(materialTypeId)
        .orElseThrow(() -> new IllegalArgumentException("Material type not found with ID: " + materialTypeId));

    if (!materialType.getStatus()) {
      throw new IllegalArgumentException("Material type is inactive (status=false): " + materialType.getName());
    }

    // Fetch the latest metal rate for this material type
    MetalRate latestRate = metalRateRepository.findLatestByMaterialType(materialType)
        .orElseThrow(() -> new IllegalStateException("No metal rate found for material type: " + materialType.getName()));

    // Calculate metal value: netWeight * ratePerGram * purity
    BigDecimal metalValue = netWeight.multiply(latestRate.getRatePerGram()).multiply(purity);

    // Calculate making charge
    BigDecimal makingCharge;
    String makingChargeType = materialType.getMakingChargeType();
    BigDecimal makingChargeValue = materialType.getMakingChargeValue();

    if ("PERCENTAGE".equalsIgnoreCase(makingChargeType)) {
      makingCharge = metalValue.multiply(makingChargeValue.divide(BigDecimal.valueOf(100)));
    } else {
      // Assume FIXED
      makingCharge = makingChargeValue;
    }

    // Calculate subtotal
    BigDecimal subtotal = metalValue.add(makingCharge);

    // Calculate GST at 3%
    BigDecimal gst = subtotal.multiply(new BigDecimal("0.03"));

    // Calculate final price
    BigDecimal finalPrice = subtotal.add(gst);

    // Return the breakdown
    return JewelryPriceBreakdown.builder()
        .metalValue(metalValue)
        .makingCharge(makingCharge)
        .gst(gst)
        .finalPrice(finalPrice)
        .build();
  }
}