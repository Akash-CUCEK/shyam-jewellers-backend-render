package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/public")
public class ProductController {

  private final ProductService productService;

  @Operation(summary = "Get all products (paginated)")
  @GetMapping("/getAllProducts")
  public BaseResponseDTO<Page<AllProductResponseDTO>> getAllProducts(Pageable pageable) {
    return new BaseResponseDTO<>(productService.getAllProduct(pageable), null);
  }

  @Operation(summary = "Get product by product id")
  @GetMapping("/getProductById/{productId}")
  public BaseResponseDTO<AllProductResponseDTO> getProductById(@PathVariable Long productId) {
    return new BaseResponseDTO<>(productService.getProductById(productId), null);
  }

  @GetMapping("/category/{category}")
  public BaseResponseDTO<PageResponseDTO<AllProductResponseDTO>> getByCategory(
      @PathVariable String category, Pageable pageable) {
    return new BaseResponseDTO<>(productService.getProductsByCategory(category, pageable), null);
  }

  @GetMapping("/materialType/{materialType}")
  public BaseResponseDTO<PageResponseDTO<AllProductResponseDTO>> getByMaterialType(
      @PathVariable String materialType, Pageable pageable) {
    log.info("Received request for getting products based on material Type");
    return new BaseResponseDTO<>(productService.getByMaterialType(materialType, pageable), null);
  }

  @Operation(summary = "Get products under given price")
  @GetMapping("/price/under")
  public BaseResponseDTO<PageResponseDTO<AllProductResponseDTO>> getProductsUnderPrice(
      @RequestParam BigDecimal price, Pageable pageable) {
    log.info("Received request for getting products under price {}", price);

    return new BaseResponseDTO<>(productService.getProductsUnderPrice(price, pageable), null);
  }

  @Operation(summary = "Get products above given price")
  @GetMapping("/price/above")
  public BaseResponseDTO<PageResponseDTO<AllProductResponseDTO>> getProductsAbovePrice(
      @RequestParam BigDecimal price, Pageable pageable) {
    log.info("Received request for getting products above price {}", price);

    return new BaseResponseDTO<>(productService.getProductsByAbovePrice(price, pageable), null);
  }

//  @Operation(summary = "Get product by name")
//  @PostMapping("/getProductsByName")
//  public BaseResponseDTO<ProductResponseDTO> getProductByName(
//      @Valid @RequestBody GetProductByNameRequestDTO dto) {
//    return new BaseResponseDTO<>(productService.getNameProduct(dto), null);
//  }

  @Operation(summary = "Get products by gender")
  @PostMapping("/getProductsByGender")
  public BaseResponseDTO<GenderResponseDTO> getProductsByGender(
      @Valid @RequestBody GenderRequestDTO dto) {
    log.info("received request for getting products based on gender");
    return new BaseResponseDTO<>(productService.getGenderProduct(dto), null);
  }

  @Operation(summary = "Get filtered products (paginated)")
  @PostMapping("/getProductsByFilter")
  public BaseResponseDTO<Page<AllProductResponseDTO>> getFilteredProducts(
      @Valid @RequestBody ProductFilterRequestDTO dto, Pageable pageable) {
    return new BaseResponseDTO<>(productService.getFilteredProducts(dto, pageable), null);
  }
}
