package com.shyam.mapper;

import com.shyam.dto.request.ProductAddRequestDTO;
import com.shyam.dto.request.UpdateRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.entity.Category;
import com.shyam.entity.Products;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  public Products toEntity(ProductAddRequestDTO dto, Category category) {
    Products p = new Products();
    p.setCategory(category);
    p.setPrice(dto.getPrice());
    p.setDiscountPercentage(dto.getDiscountPercentage());
    p.setWeight(dto.getWeight());
    p.setMaterialType(dto.getMaterialType());
    p.setGender(dto.getGender());
    p.setShortDescription(dto.getShortDescription());
    p.setFullDescription(dto.getFullDescription());
    p.setIsAvailable(dto.getIsAvailable());
    p.setQuantity(dto.getQuantity());
    p.setAvailableStock(dto.getQuantity());
    p.setCreatedBy(dto.getEmail());
    p.setUpdatedBy(dto.getEmail());

    return p;
  }

  public void updateEntity(Products p, UpdateRequestDTO dto, Category category) {
    p.setCategory(category); // ✅ correct
    p.setPrice(dto.getPrice());
    p.setDiscountPercentage(dto.getDiscountPercentage());
    p.setWeight(dto.getWeight());
    p.setMaterialType(dto.getMaterialType());
    p.setShortDescription(dto.getShortDescription());
    p.setFullDescription(dto.getFullDescription());
    p.setGender(dto.getGender());
    p.setIsAvailable(dto.getIsAvailable());
    p.setQuantity(dto.getQuantity());
    p.setAvailableStock(dto.getAvailableStock());
    p.setImageUrl(dto.getImageUrl());
    p.setUpdatedBy(dto.getUpdatedBy());
  }

//  public ProductResponseDTO toProductResponse(Products p) {
//    return ProductResponseDTO.builder()
//            .id(p.getProductIds())
//            .name(p.getName())
//            .category(p.getCategory().getName()) // ✅ correct
//            .price(p.getPrice())
//            .discountPercentage(p.getDiscountPercentage())
//            .weight(p.getWeight())
//            .materialType(p.getMaterialType())
//            .skuCode(p.getSkuCode())
//            .shortDescription(p.getShortDescription())
//            .fullDescription(p.getFullDescription())
//            .gender(p.getGender())
//            .averageRating(p.getAverageRating())
//            .isAvailable(p.getIsAvailable())
//            .availableStock(p.getAvailableStock())
//            .imageUrl(p.getImageUrl())
//            .build();
//  }

  public AllProductResponseDTO toAllProductResponse(Products p) {

    BigDecimal finalPrice =
        p.getDiscountPercentage() != null
            ? p.getPrice()
                .subtract(
                    p.getPrice()
                        .multiply(BigDecimal.valueOf(p.getDiscountPercentage()))
                        .divide(BigDecimal.valueOf(100)))
            : p.getPrice();

    return AllProductResponseDTO.builder()
        .id(p.getProductIds())
        .name(p.getName())
        .price(p.getPrice())
        .discountPercentage(p.getDiscountPercentage())
        .finalPrice(finalPrice)
        .weight(p.getWeight())
        .imageUrl(p.getImageUrl())
        .gender(p.getGender())
        .isAvailable(p.getIsAvailable())
        .availableStock(p.getAvailableStock())
        .build();
  }

  public ProductResponseDTO toProductResponse(Products p) {
    return ProductResponseDTO.builder()
            .id(p.getProductIds())
            .name(p.getName())
            .category(p.getCategory().getName())
            .price(p.getPrice())
            .discountPercentage(p.getDiscountPercentage())
            .weight(p.getWeight())
            .materialType(p.getMaterialType())
            .skuCode(p.getSkuCode())
            .shortDescription(p.getShortDescription())
            .fullDescription(p.getFullDescription())
            .gender(p.getGender())
            .averageRating(p.getAverageRating())
            .isAvailable(p.getIsAvailable())
            .availableStock(p.getAvailableStock())
            .imageUrl(p.getImageUrl())
            .build();
  }

  public ProductAddResponseDTO toAddProductResponse(Products p, String msg) {
    return ProductAddResponseDTO.builder().name(p.getName()).message(msg).build();
  }

  public UpdateResponseDTO toUpdateProductResponse(String msg) {
    return UpdateResponseDTO.builder().message(msg).build();
  }

  public DeleteResponseDTO toDeleteProductResponse(String msg) {
    return DeleteResponseDTO.builder().message(msg).build();
  }

  public GetProductResponseDTO toProductListResponse(List<Products> products) {
    return GetProductResponseDTO.builder()
        .products(products.stream().map(this::toAllProductResponse).toList())
        .build();
  }

  public GenderResponseDTO toGenderResponse(List<Products> products) {
    return GenderResponseDTO.builder()
        .products(products.stream().map(this::toAllProductResponse).toList())
        .build();
  }

  public GetAllProductsResponseDTO toGetAllProductsResponse(Products p) {
    return GetAllProductsResponseDTO.builder()
        .productIds(p.getProductIds())
        .name(p.getName())
        .category(p.getCategory())
        .price(p.getPrice())
        .weight(p.getWeight())
        .materialType(p.getMaterialType())
        .isAvailable(p.getIsAvailable())
        .imageUrl(p.getImageUrl())
        .build();
  }
}
