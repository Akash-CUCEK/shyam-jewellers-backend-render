package com.shyam.service;

// DTO for cart item
public class CartItem {
  private Long variantId;
  private Integer quantity;

  // Getters and setters
  public Long getVariantId() {
    return variantId;
  }

  public void setVariantId(Long variantId) {
    this.variantId = variantId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}
