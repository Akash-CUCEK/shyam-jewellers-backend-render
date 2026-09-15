package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.GetCategoryByIdRequestDTO;
import com.shyam.dto.request.GetMaterialTypeByIdRequestDTO;
import com.shyam.dto.request.GetProductByIdRequestDTO;
import com.shyam.dto.response.GetCategoryUserResponseDTO;
import com.shyam.dto.response.GetMaterialTypeResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import com.shyam.service.CategoryService;
import com.shyam.service.MaterialTypeService;
import com.shyam.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/public")
@Tag(name = "Product Public", description = "Public product endpoints")
public class ProductPublicController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final MaterialTypeService materialTypeService;

    @Operation(summary = "Get all active products", description = "Retrieve a paginated list of active products.")
    @GetMapping("/products")
    public BaseResponseDTO<Page<GetProductResponseDTO>> getAllActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long materialTypeId) {
        log.info("Received request to get all active products");
        Pageable pageable = PageRequest.of(page, size);
        // For public endpoints, only show ACTIVE products
        // Convert IDs to names for service method
        String categoryName = null;
        String materialTypeName = null;
        if (categoryId != null) {
            GetCategoryByIdRequestDTO categoryRequest = GetCategoryByIdRequestDTO.builder()
                    .id(categoryId)
                    .build();
            GetCategoryUserResponseDTO categoryResponse = categoryService.getCategoryUser(categoryRequest);
            if (categoryResponse != null) {
                categoryName = categoryResponse.getName();
            }
        }
        if (materialTypeId != null) {
            GetMaterialTypeByIdRequestDTO materialTypeRequest = GetMaterialTypeByIdRequestDTO.builder()
                    .materialTypeId(materialTypeId)
                    .build();
            GetMaterialTypeResponseDTO materialTypeResponse = materialTypeService.getMaterialTypeById(materialTypeRequest);
            if (materialTypeResponse != null) {
                materialTypeName = materialTypeResponse.getName();
            }
        }
        Page<GetProductResponseDTO> response = productService.getAllProducts(
                page, size,
                categoryName,
                materialTypeName,
                "ACTIVE",
                pageable);
        return new BaseResponseDTO<>(response, null);
    }

    @Operation(summary = "Get product by ID", description = "Get product details by ID if status is ACTIVE.")
    @GetMapping("/products/{productId}")
    public BaseResponseDTO<GetProductResponseDTO> getProductById(@PathVariable Long productId) {
        log.info("Received request to get product by ID: {}", productId);
        // First get the product to check its status
        GetProductByIdRequestDTO productRequest = GetProductByIdRequestDTO.builder()
                .productId(productId)
                .build();
        GetProductResponseDTO product = productService.getProductById(productRequest);

        // Only return if status is ACTIVE
        if ("ACTIVE".equalsIgnoreCase(product.getStatus())) {
            return new BaseResponseDTO<>(product, null);
        } else {
            // Return 404-like response for inactive/draft products
            return new BaseResponseDTO<>(null, "Product not available");
        }
    }
}