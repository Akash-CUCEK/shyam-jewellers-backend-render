package com.shyam.service.Imp;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dto.PriceBreakdownDTO;
import com.shyam.entity.MaterialType;
import com.shyam.entity.MetalRate;
import com.shyam.entity.Product;
import com.shyam.entity.ProductVariant;
import com.shyam.entity.Purity;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.MetalRateRepository;
import com.shyam.repository.ProductVariantRepository;
import com.shyam.repository.PurityRepository;
import com.shyam.service.PricingService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

  private final ProductVariantRepository productVariantRepository;
  private final MetalRateRepository metalRateRepository;
  private final PurityRepository purityRepository;
  private final MaterialTypeRepository materialTypeRepository;
  private final MessageSourceUtil messageSourceUtil;

  @Override
  public PriceBreakdownDTO calculatePrice(ProductVariant variant) {
    // 1. Validate variant and its relationships
    if (variant == null) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "VARIANT_NULL",
          "Product variant is null",
          "Product variant cannot be null");
    }

    Product product = variant.getProduct();
    if (product == null) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "PRODUCT_NULL",
          "Product is null for variant",
          "Product variant must have an associated product");
    }

    // 2. Get MaterialType from product
    MaterialType materialType = product.getMaterialType();
    if (materialType == null) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "MATERIAL_TYPE_NULL",
          "Material type is null for product",
          "Product must have an associated material type");
    }

    // 3. Get LATEST MetalRate for the material type
    MetalRate metalRate =
        metalRateRepository
            .findLatestByMaterialType(materialType)
            .orElseThrow(
                () ->
                    new SYMException(
                        HttpStatus.BAD_REQUEST,
                        SYMErrorType.VALIDATION_FAILED,
                        "METAL_RATE_NOT_FOUND",
                        "Metal rate not available, please try again later",
                        "No metal rate found for material type: " + materialType.getName()));

    // 4. Check if metal rate is stale (older than 24 hours) - log warning only
    LocalDateTime fetchedAt = metalRate.getFetchedAt();
    if (fetchedAt != null) {
      Duration duration = Duration.between(fetchedAt, LocalDateTime.now());
      if (duration.toHours() > 24) {
        log.warn(
            "Metal rate for {} is stale (fetched at {}), consider updating",
            materialType.getName(),
            fetchedAt);
      }
    }

    // 5. Get purity factor from product's purity
    Purity purity = product.getPurity();
    if (purity == null) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "PURITY_NOT_FOUND",
          "Purity not found for product",
          "Product must have an associated purity");
    }

    // 6. Validate required fields are not null
    BigDecimal weight = variant.getWeight();
    if (weight == null) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "WEIGHT_NULL",
          "Weight is null for product variant",
          "Product variant weight cannot be null");
    }

    BigDecimal purityFactor = purity.getPurityFactor();
    if (purityFactor == null) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "PURITY_FACTOR_NULL",
          "Purity factor is null for product",
          "Product purity factor cannot be null");
    }

    BigDecimal makingChargeValue = product.getMakingChargeValue();
    if (makingChargeValue == null) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "MAKING_CHARGE_VALUE_NULL",
          "Making charge value is null for product",
          "Product making charge value cannot be null");
    }

    // 7. Get metal rate per gram
    BigDecimal ratePerGram = metalRate.getRatePerGram();
    if (ratePerGram == null) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "METAL_RATE_NULL",
          "Metal rate per gram is null",
          "Metal rate per gram cannot be null");
    }

    // 8. Calculate metal value: weight × metalRatePerGram × purityFactor
    BigDecimal metalValue = weight.multiply(ratePerGram).multiply(purityFactor);
    // Round to 2 decimal places for currency
    metalValue = metalValue.setScale(2, BigDecimal.ROUND_HALF_UP);

    // 9. Calculate making charge
    String makingChargeType = product.getMakingChargeType();
    BigDecimal makingCharge;
    if ("PERCENTAGE".equalsIgnoreCase(makingChargeType)) {
      // makingCharge = metalValue × (makingChargeValue / 100)
      makingCharge = metalValue.multiply(makingChargeValue.divide(BigDecimal.valueOf(100)));
    } else if ("FIXED".equalsIgnoreCase(makingChargeType)) {
      // makingCharge = makingChargeValue (fixed amount)
      makingCharge = makingChargeValue;
    } else if ("PER_GRAM".equalsIgnoreCase(makingChargeType)) {
      // makingCharge = weight × makingChargeValue
      makingCharge = weight.multiply(makingChargeValue);
    } else {
      // Default to FIXED if type is not recognized
      log.warn("Unknown making charge type: {}, defaulting to FIXED", makingChargeType);
      makingCharge = makingChargeValue;
    }
    // Round makingCharge to 2 decimal places
    makingCharge = makingCharge.setScale(2, BigDecimal.ROUND_HALF_UP);

    // 10. Calculate subtotal (metalValue + makingCharge)
    BigDecimal subtotal = metalValue.add(makingCharge);

    // 11. Calculate GST (3% of subtotal)
    BigDecimal gst = subtotal.multiply(new BigDecimal("0.03"));
    // Round GST to 2 decimal places
    gst = gst.setScale(2, BigDecimal.ROUND_HALF_UP);

    // 12. Calculate final price (subtotal + gst)
    BigDecimal finalPrice = subtotal.add(gst);
    // Round final price to 2 decimal places
    finalPrice = finalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);

    // 13. Create and return PriceBreakdownDTO
    return PriceBreakdownDTO.builder()
        .metalValue(metalValue)
        .makingCharge(makingCharge)
        .gst(gst)
        .finalPrice(finalPrice)
        .ratePerGramUsed(ratePerGram)
        .purityFactorUsed(purityFactor)
        .build();
  }
}
