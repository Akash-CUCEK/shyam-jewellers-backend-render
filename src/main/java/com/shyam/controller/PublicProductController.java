package com.shyam.controller;

import com.shyam.common.constants.ProductStatus;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dto.request.GetProductByIdRequestDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import com.shyam.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/products")
@Tag(name = "Product Public", description = "Public product endpoints")
public class PublicProductController {

  private final ProductService productService;

  @Operation(
      summary = "Get all active products",
      description = "Retrieve a paginated list of ACTIVE products only.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping
  public BaseResponseDTO<Page<GetProductResponseDTO>> getAllActiveProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long materialTypeId) {
    log.info(
        "Entering getAllActiveProducts method with page: {}, size: {}, categoryId: {}, materialTypeId: {}",
        page,
        size,
        categoryId,
        materialTypeId);
    log.info("Received request to get all active products, page: {}, size: {}", page, size);
    Pageable pageable = PageRequest.of(page, size);
    // Direct ID-based filtering — no extra service calls to resolve names
    Page<GetProductResponseDTO> response =
        productService.getAllProducts(categoryId, materialTypeId, ProductStatus.ACTIVE, pageable);
    log.info("Exiting getAllActiveProducts method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get product by ID",
      description = "Get product details by ID, only if status is ACTIVE.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Product not found or not active",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/{productId}")
  public BaseResponseDTO<GetProductResponseDTO> getProductById(@PathVariable Long productId) {
    log.info("Entering getProductById method with productId: {}", productId);
    log.info("Received request to get product by id: {}", productId);
    GetProductByIdRequestDTO requestDTO =
        GetProductByIdRequestDTO.builder().productId(productId).build();
    GetProductResponseDTO product = productService.getProductById(requestDTO);

    if (!ProductStatus.ACTIVE.name().equals(product.getStatus())) {
      log.error("Product with id {} is not ACTIVE", productId);
      throw new SYMException(
          HttpStatus.NOT_FOUND,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND_BY_MAIL,
          "Product not found",
          "Product with id " + productId + " is not ACTIVE");
    }

    log.info("Exiting getProductById method");
    return new BaseResponseDTO<>(product, null);
  }
}
