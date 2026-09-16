package com.shyam.service;

import com.shyam.dto.PriceBreakdownDTO;
import com.shyam.entity.ProductVariant;

/** Service for calculating jewelry prices based on metal rate, purity, making charges, and GST. */
public interface PricingService {

  /**
   * Calculates the price for a product variant based on current metal rate, purity, making charges,
   * and GST.
   *
   * @param variant the product variant to calculate price for
   * @return PriceBreakdownDTO containing the price breakdown
   * @throws IllegalArgumentException if variant is not valid or required data is missing
   */
  PriceBreakdownDTO calculatePrice(ProductVariant variant);
}
