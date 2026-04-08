package com.shyam.dao;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dto.request.ProductFilterRequestDTO;
import com.shyam.entity.Products;
import com.shyam.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDAO {

  private final ProductRepository productRepository;

  public Products save(Products product) {
    return productRepository.save(product);
  }

  public void delete(Products product) {
    productRepository.delete(product);
  }

  public Products findProduct(String name) {
    return productRepository
        .findByName(name)
        .orElseThrow(
            () ->
                new SYMException(
                    HttpStatus.NOT_FOUND,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_PRODUCT_NOT_FOUND,
                    "Product not found: " + name,
                    "No product in DB with name: " + name));
  }

  public List<Products> getGenderProduct(String gender) {
    return productRepository.findProductByGender(gender);
  }

  public Page<Products> getProductsByCategory(String category, Pageable pageable) {
    return productRepository.findByCategoryAndIsAvailableTrue(category, pageable);
  }

  public Page<Products> getAllProduct(Pageable pageable) {
    return productRepository.findAll(pageable);
  }

  public Page<Products> findProductsByFilters(ProductFilterRequestDTO dto, Pageable pageable) {
    return productRepository.findProductsByFilters(
        dto.getCategory(),
        dto.getMinPrice(),
        dto.getMaxPrice(),
        dto.getMinWeight(),
        dto.getMaxWeight(),
        dto.getMaterialType(),
        dto.getGender(),
        dto.getIsAvailable(),
        dto.getMinAvailableStock(),
        dto.getMaxAvailableStock(),
        pageable);
  }

  public Optional<Products> getProductById(Long productId) {
    return productRepository.findById(productId);
  }

  public Page<Products> getProductsByMaterialType(String materialType, Pageable pageable) {
    return productRepository.getProductsByMaterialType(materialType, pageable);
  }

  public Page<Products> getProductsUnderPrice(BigDecimal price, Pageable pageable) {
    return productRepository.findProductsUnderPrice(price, pageable);
  }

  public Page<Products> getProductsAbovePrice(BigDecimal price, Pageable pageable) {
    return productRepository.findProductsAbovePrice(price, pageable);
  }
}
