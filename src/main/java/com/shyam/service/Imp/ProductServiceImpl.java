package com.shyam.service.Imp;

import com.shyam.common.constants.ProductStatus;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dto.request.*;
import com.shyam.dto.response.AddProductResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import com.shyam.entity.Category;
import com.shyam.entity.MaterialType;
import com.shyam.entity.Product;
import com.shyam.entity.Purity;
import com.shyam.mapper.ProductMapper;
import com.shyam.repository.CategoryRepository;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.ProductRepository;
import com.shyam.repository.PurityRepository;
import com.shyam.service.ProductService;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final MaterialTypeRepository materialTypeRepository;
  private final PurityRepository purityRepository;
  private final MessageSourceUtil messageSourceUtil;
  private final ProductMapper productMapper;

  @Override
  @Transactional
  public AddProductResponseDTO addProduct(AddProductRequestDTO requestDTO) {
    log.info("Processing request to add product");

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

    Product product =
        Product.builder()
            .category(category)
            .materialType(materialType)
            .purity(purity)
            .makingChargeType("PERCENTAGE")
            .makingChargeValue(requestDTO.getMakingChargeValue())
            .description(requestDTO.getDescription())
            .status(ProductStatus.DRAFT)
            .hallmarkCertified(requestDTO.getHallmarkCertified())
            .certificationNumber(requestDTO.getCertificationNumber())
            .discountType(requestDTO.getDiscountType())
            .discountValue(requestDTO.getDiscountValue())
            .discountValidTill(requestDTO.getDiscountValidTill())
            .createdBy(requestDTO.getCreatedBy())
            .createdAt(LocalDateTime.now())
            .build();

    Product saved = productRepository.save(product);

    saved.setProductName(generateProductName(category, materialType, purity, saved.getProductId()));
    productRepository.save(saved);

    log.info("Product added successfully with id: {}", saved.getProductId());
    return productMapper.mapToProduct(messageSourceUtil.getMessage("product.added"));
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
