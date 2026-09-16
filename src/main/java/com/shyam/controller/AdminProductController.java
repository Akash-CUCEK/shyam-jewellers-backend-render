package com.shyam.controller;

import com.shyam.common.constants.ProductStatus;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.AddProductResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import com.shyam.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin Product", description = "Admin product management endpoints")
public class AdminProductController {

  private final ProductService productService;

  @Operation(summary = "Add product", description = "Add a new product.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Product added successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "Category/MaterialType/Purity not found",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<AddProductResponseDTO> addProduct(
      @Valid @RequestBody AddProductRequestDTO requestDTO) {
    log.info("Entering addProduct method");
    log.info("Received request to add product");
    var response = productService.addProduct(requestDTO);
    log.info("Exiting addProduct method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Update product", description = "Update an existing product.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Product updated successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "Product/Category/MaterialType/Purity not found",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PutMapping("/{productId}")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<AddProductResponseDTO> updateProduct(
      @PathVariable Long productId, @Valid @RequestBody UpdateProductRequestDTO requestDTO) {
    log.info("Entering updateProduct method with productId: {}", productId);
    requestDTO.setProductId(productId);
    log.info("Received request to update product id: {}", productId);
    var response = productService.updateProduct(requestDTO);
    log.info("Exiting updateProduct method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Delete product",
      description = "Soft-delete a product by setting status to INACTIVE.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Product deleted successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @DeleteMapping("/{productId}")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<AddProductResponseDTO> deleteProduct(
      @PathVariable Long productId, @RequestParam String updatedBy) {
    log.info("Entering deleteProduct method with productId: {}", productId);
    log.info("Received request to delete product id: {}", productId);
    DeleteProductRequestDTO requestDTO =
        DeleteProductRequestDTO.builder().productId(productId).updatedBy(updatedBy).build();
    var response = productService.deleteProduct(requestDTO);
    log.info("Exiting deleteProduct method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get all products (Admin view)",
      description =
          "Paginated list of all products, any status, filterable by category/material type/status.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<Page<GetProductResponseDTO>> getAllProductsAdmin(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long materialTypeId,
      @RequestParam(required = false) ProductStatus status) {
    log.info(
        "Entering getAllProductsAdmin method with page: {}, size: {}, categoryId: {}, materialTypeId: {}, status: {}",
        page,
        size,
        categoryId,
        materialTypeId,
        status);
    log.info("Received request to get all products for admin, page: {}, size: {}", page, size);
    Pageable pageable = PageRequest.of(page, size);
    Page<GetProductResponseDTO> response =
        productService.getAllProducts(categoryId, materialTypeId, status, pageable);
    log.info("Exiting getAllProductsAdmin method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get product by ID (Admin view)",
      description = "Get product details by ID, any status.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/{productId}")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<GetProductResponseDTO> getProductByIdAdmin(@PathVariable Long productId) {
    log.info("Entering getProductByIdAdmin method with productId: {}", productId);
    log.info("Received request to get product by id: {}", productId);
    GetProductByIdRequestDTO requestDTO =
        GetProductByIdRequestDTO.builder().productId(productId).build();
    var response = productService.getProductById(requestDTO);
    log.info("Exiting getProductByIdAdmin method");
    return new BaseResponseDTO<>(response, null);
  }
}
