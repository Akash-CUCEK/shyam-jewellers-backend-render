package com.shyam.service.Imp;

import com.shyam.common.constants.ProductStatus;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dto.request.*;
import com.shyam.dto.response.AddProductResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import com.shyam.entity.*;
import com.shyam.mapper.ProductMapper;
import com.shyam.repository.*;
import com.shyam.service.ProductService;
import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductVariantRepository productVariantRepository;
  private final ProductImageRepository productImageRepository;
  private final TagRepository tagRepository;

  private final CategoryRepository categoryRepository;
  private final MaterialTypeRepository materialTypeRepository;
  private final PurityRepository purityRepository;

  private final CloudinaryService cloudinaryService;

  private final ProductMapper productMapper;
  private final MessageSourceUtil messageSourceUtil;

  @Override
  @Transactional
  public AddProductResponseDTO addProduct(
          AddProductRequestDTO requestDTO,
          List<MultipartFile> images) {

    log.info(
            "Creating product for categoryId={}, materialTypeId={}, purityId={}",
            requestDTO.getCategoryId(),
            requestDTO.getMaterialTypeId(),
            requestDTO.getPurityId());

    // =========================================================
    // 1. LOAD MASTER DATA
    // =========================================================

    Category category =
            categoryRepository
                    .findById(requestDTO.getCategoryId())
                    .orElseThrow(
                            () -> notFound(
                                    "Category not found with id: "
                                            + requestDTO.getCategoryId()));

    MaterialType materialType =
            materialTypeRepository
                    .findById(requestDTO.getMaterialTypeId())
                    .orElseThrow(
                            () -> notFound(
                                    "Material type not found with id: "
                                            + requestDTO.getMaterialTypeId()));

    Purity purity =
            purityRepository
                    .findById(requestDTO.getPurityId())
                    .orElseThrow(
                            () -> notFound(
                                    "Purity not found with id: "
                                            + requestDTO.getPurityId()));

    validatePurityBelongsToMaterialType(
            purity,
            materialType);

    // =========================================================
    // 2. BUSINESS VALIDATION
    // =========================================================

    validateHallmark(requestDTO);
    validateDiscount(requestDTO);
    validateVariants(requestDTO.getVariants());
    validateImages(images);

    // =========================================================
    // 3. CREATE PRODUCT
    // =========================================================

    /*
     * product_name is NOT NULL and actual name depends on
     * generated productId.
     *
     * Temporary unique value is used for initial insert.
     */
    Product product =
            Product.builder()
                    .productName(
                            "TEMP-" + UUID.randomUUID())
                    .category(category)
                    .materialType(materialType)
                    .purity(purity)
                    .makingChargeType("PERCENTAGE")
                    .makingChargeValue(
                            requestDTO.getMakingChargeValue())
                    .description(
                            requestDTO.getDescription())
                    .status(ProductStatus.DRAFT)
                    .hallmarkCertified(
                            requestDTO.getHallmarkCertified())
                    .certificationNumber(
                            requestDTO.getCertificationNumber())
                    .discountType(
                            requestDTO.getDiscountType())
                    .discountValue(
                            requestDTO.getDiscountValue())
                    .discountValidTill(
                            requestDTO.getDiscountValidTill())
                    .createdBy(
                            requestDTO.getCreatedBy())
                    .createdAt(
                            LocalDateTime.now())
                    .build();

    Product savedProduct =
            productRepository.saveAndFlush(product);

    // =========================================================
    // 4. GENERATE PRODUCT NAME
    // =========================================================

    String productName =
            generateProductName(
                    category,
                    materialType,
                    purity,
                    savedProduct.getProductId());

    savedProduct.setProductName(productName);

    savedProduct =
            productRepository.save(savedProduct);

    // =========================================================
    // 5. SAVE VARIANTS
    // =========================================================

    saveVariants(
            savedProduct,
            requestDTO.getVariants());

    // =========================================================
    // 6. UPLOAD AND SAVE IMAGES
    // =========================================================

    saveImages(
            savedProduct,
            images,
            requestDTO.getPrimaryImageIndex());

    // =========================================================
    // 7. ATTACH TAGS
    // =========================================================

    if (requestDTO.getTagIds() != null
            && !requestDTO.getTagIds().isEmpty()) {

      saveTags(
              savedProduct,
              requestDTO.getTagIds());
    }

    log.info(
            "Product created successfully, productId={}",
            savedProduct.getProductId());

    return productMapper.mapToProduct(
            messageSourceUtil.getMessage("product.added"));
  }

  // =============================================================
  // VARIANTS
  // =============================================================

  private void saveVariants(
          Product product,
          List<ProductVariantRequestDTO> variants) {

    for (ProductVariantRequestDTO request : variants) {

      ProductVariant variant =
              ProductVariant.builder()
                      .skuCode(
                              "TEMP-SKU-"
                                      + UUID.randomUUID())
                      .product(product)
                      .weight(request.getWeight())
                      .size(request.getSize())
                      .quantity(request.getQuantity())
                      .reservedQuantity(0)
                      .status("ACTIVE")
                      .createdAt(LocalDateTime.now())
                      .createdBy(product.getCreatedBy())
                      .build();

      ProductVariant savedVariant =
              productVariantRepository
                      .saveAndFlush(variant);

      String sku =
              generateSkuCode(
                      product,
                      savedVariant.getVariantId());

      savedVariant.setSkuCode(sku);

      productVariantRepository.save(savedVariant);
    }
  }

  // =============================================================
  // IMAGES
  // =============================================================

  private void saveImages(
          Product product,
          List<MultipartFile> images,
          Integer primaryImageIndex) {

    int primaryIndex =
            primaryImageIndex == null
                    ? 0
                    : primaryImageIndex;

    if (primaryIndex < 0
            || primaryIndex >= images.size()) {

      throw new IllegalArgumentException(
              "Invalid primary image index");
    }

    for (int index = 0;
         index < images.size();
         index++) {

      MultipartFile image =
              images.get(index);

      String imageUrl =
              cloudinaryService.upload(image);

      ProductImage productImage =
              ProductImage.builder()
                      .product(product)
                      .imageUrl(imageUrl)
                      .primary(index == primaryIndex)
                      .displayOrder(index)
                      .createdAt(LocalDateTime.now())
                      .createdBy(product.getCreatedBy())
                      .build();

      productImageRepository.save(productImage);
    }
  }

  // =============================================================
  // TAGS
  // =============================================================

  private void saveTags(
          Product product,
          List<Long> tagIds) {

    List<Tag> tags =
            tagRepository
                    .findAllByTagIdInAndActiveTrue(tagIds);

    if (tags.size() != tagIds.size()) {

      Set<Long> foundTagIds =
              new HashSet<>();

      for (Tag tag : tags) {
        foundTagIds.add(tag.getTagId());
      }

      List<Long> missingTagIds =
              tagIds.stream()
                      .filter(id -> !foundTagIds.contains(id))
                      .toList();

      throw notFound(
              "Tag(s) not found or inactive: "
                      + missingTagIds);
    }

    product.setTags(
            new ArrayList<>(tags));

    productRepository.save(product);
  }

  // =============================================================
  // VALIDATIONS
  // =============================================================

  private void validateImages(
          List<MultipartFile> images) {

    if (images == null || images.isEmpty()) {
      throw new IllegalArgumentException(
              "At least one product image is required");
    }

    if (images.size() > 5) {
      throw new IllegalArgumentException(
              "Maximum 5 product images are allowed");
    }

    for (MultipartFile image : images) {

      if (image == null || image.isEmpty()) {
        throw new IllegalArgumentException(
                "Product image cannot be empty");
      }

      String contentType =
              image.getContentType();

      if (contentType == null
              || !contentType.startsWith("image/")) {

        throw new IllegalArgumentException(
                "Only image files are allowed");
      }
    }
  }

  private void validateVariants(
          List<ProductVariantRequestDTO> variants) {

    if (variants == null || variants.isEmpty()) {
      throw new IllegalArgumentException(
              "At least one product variant is required");
    }
  }

  private void validateHallmark(
          AddProductRequestDTO requestDTO) {

    if (Boolean.TRUE.equals(
            requestDTO.getHallmarkCertified())) {

      if (requestDTO.getCertificationNumber() == null
              || requestDTO.getCertificationNumber().isBlank()) {

        throw new IllegalArgumentException(
                "Certification number is required when hallmark is enabled");
      }
    }
  }

  private void validateDiscount(
          AddProductRequestDTO requestDTO) {

    boolean discountProvided =
            requestDTO.getDiscountType() != null
                    || requestDTO.getDiscountValue() != null
                    || requestDTO.getDiscountValidTill() != null;

    if (!discountProvided) {
      return;
    }

    if (requestDTO.getDiscountType() == null
            || requestDTO.getDiscountType().isBlank()) {

      throw new IllegalArgumentException(
              "Discount type is required");
    }

    if (requestDTO.getDiscountValue() == null
            || requestDTO.getDiscountValue().signum() <= 0) {

      throw new IllegalArgumentException(
              "Discount value must be positive");
    }

    if (requestDTO.getDiscountValidTill() == null) {

      throw new IllegalArgumentException(
              "Discount valid till is required");
    }
  }

  // =============================================================
  // SKU
  // =============================================================

  private String generateSkuCode(
          Product product,
          Long variantId) {

    return "SJ-"
            + product.getProductId()
            + "-"
            + variantId;
  }


  @Override
  @Transactional
  public AddProductResponseDTO updateProduct(UpdateProductRequestDTO requestDTO) {
    log.info("Processing request to update product id: {}", requestDTO.getProductId());

    Product existing =
        productRepository
            .findById(requestDTO.getProductId())
            .orElseThrow(() -> notFound("Product not found with id: " + requestDTO.getProductId()));

    Category category =
        categoryRepository
            .findById(requestDTO.getCategoryId())
            .orElseThrow(
                () -> notFound("Category not found with id: " + requestDTO.getCategoryId()));

    MaterialType materialType =
        materialTypeRepository
            .findById(requestDTO.getMaterialTypeId())
            .orElseThrow(
                () ->
                    notFound("Material type not found with id: " + requestDTO.getMaterialTypeId()));

    Purity purity =
        purityRepository
            .findById(requestDTO.getPurityId())
            .orElseThrow(() -> notFound("Purity not found with id: " + requestDTO.getPurityId()));

    validatePurityBelongsToMaterialType(purity, materialType);

    existing.setCategory(category);
    existing.setMaterialType(materialType);
    existing.setPurity(purity);
    existing.setMakingChargeValue(requestDTO.getMakingChargeValue());
    existing.setDescription(requestDTO.getDescription());
    existing.setHallmarkCertified(requestDTO.getHallmarkCertified());
    existing.setCertificationNumber(requestDTO.getCertificationNumber());
    existing.setDiscountType(requestDTO.getDiscountType());
    existing.setDiscountValue(requestDTO.getDiscountValue());
    existing.setDiscountValidTill(requestDTO.getDiscountValidTill());
    existing.setUpdatedBy(requestDTO.getUpdatedBy());
    existing.setUpdatedAt(LocalDateTime.now());
    existing.setProductName(
        generateProductName(category, materialType, purity, existing.getProductId()));

    productRepository.save(existing);

    log.info("Product updated successfully with id: {}", existing.getProductId());
    return productMapper.mapToProduct(messageSourceUtil.getMessage("product.updated"));
  }

  @Override
  @Transactional
  public AddProductResponseDTO deleteProduct(DeleteProductRequestDTO requestDTO) {
    log.info("Processing request to delete product id: {}", requestDTO.getProductId());

    Product existing =
        productRepository
            .findById(requestDTO.getProductId())
            .orElseThrow(() -> notFound("Product not found with id: " + requestDTO.getProductId()));

    existing.setStatus(ProductStatus.INACTIVE);
    existing.setUpdatedBy(requestDTO.getUpdatedBy());
    existing.setUpdatedAt(LocalDateTime.now());
    productRepository.save(existing);

    log.info("Product soft-deleted (INACTIVE) successfully with id: {}", existing.getProductId());
    return productMapper.mapToProduct(messageSourceUtil.getMessage("product.deleted"));
  }

  @Override
  public GetProductResponseDTO getProductById(GetProductByIdRequestDTO requestDTO) {
    Product product =
        productRepository
            .findById(requestDTO.getProductId())
            .orElseThrow(() -> notFound("Product not found with id: " + requestDTO.getProductId()));
    return GetProductResponseDTO.fromEntity(product);
  }

  @Override
  public Page<GetProductResponseDTO> getAllProducts(
      Long categoryId, Long materialTypeId, ProductStatus status, Pageable pageable) {

    Specification<Product> spec = Specification.where(null);

    if (categoryId != null) {
      spec =
          spec.and(
              (root, query, cb) -> cb.equal(root.get("category").get("categoryId"), categoryId));
    }
    if (materialTypeId != null) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.equal(root.get("materialType").get("materialTypeId"), materialTypeId));
    }
    if (status != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }

    Page<Product> productPage = productRepository.findAll(spec, pageable);
    return productPage.map(GetProductResponseDTO::fromEntity);
  }

  // ===== Helper methods =====

  private String generateProductName(
      Category category, MaterialType materialType, Purity purity, Long productId) {
    return String.format(
        "%s %s %s #%d",
        category.getName(), materialType.getName(), purity.getPurityName(), productId);
  }

  private void validatePurityBelongsToMaterialType(Purity purity, MaterialType materialType) {
    if (!Objects.equals(
        purity.getMaterialType().getMaterialTypeId(), materialType.getMaterialTypeId())) {
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          ErrorCodeConstants.ERROR_CODE_VALIDATION,
          "Selected purity does not belong to the selected material type.",
          "Purity-MaterialType mismatch");
    }
  }

  private SYMException notFound(String message) {
    return new SYMException(
        HttpStatus.NOT_FOUND,
        SYMErrorType.GENERIC_EXCEPTION,
        ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND_BY_MAIL,
        message,
        message);
  }
}
