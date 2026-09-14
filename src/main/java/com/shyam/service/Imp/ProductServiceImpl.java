package com.shyam.service.Imp;

import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dto.request.AddProductRequestDTO;
import com.shyam.dto.request.GetProductByIdRequestDTO;
import com.shyam.dto.request.UpdateProductRequestDTO;
import com.shyam.dto.response.AddProductResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import com.shyam.entity.Category;
import com.shyam.entity.MaterialType;
import com.shyam.entity.Purity;
import com.shyam.entity.Product;
import com.shyam.mapper.ProductMapper;
import com.shyam.repository.CategoryRepository;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.ProductRepository;
import com.shyam.repository.PurityRepository;
import com.shyam.service.ProductService;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
        // Validate category exists
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Validate material type exists
        MaterialType materialType = materialTypeRepository.findById(requestDTO.getMaterialTypeId())
                .orElseThrow(() -> new RuntimeException("Material type not found"));

        // Validate purity exists
        Purity purity = purityRepository.findById(requestDTO.getPurityId())
                .orElseThrow(() -> new RuntimeException("Purity not found"));

        // Validate purity belongs to selected material type
        if (!Objects.equals(purity.getMaterialType().getMaterialTypeId(), materialType.getMaterialTypeId())) {
            throw new RuntimeException("Purity does not belong to selected material type");
        }

        // Create product without productName (will be generated after getting ID)
        Product product = Product.builder()
                .category(category)
                .materialType(materialType)
                .purity(purity)
                .makingChargeType("PERCENTAGE") // As per user, only PERCENTAGE for now
                .makingChargeValue(requestDTO.getMakingChargeValue())
                .description(requestDTO.getDescription())
                .status("DRAFT") // Default status, user can change later via update
                .hallmarkCertified(requestDTO.getHallmarkCertified())
                .certificationNumber(requestDTO.getCertificationNumber())
                .discountType(requestDTO.getDiscountType())
                .discountValue(requestDTO.getDiscountValue())
                .discountValidTill(requestDTO.getDiscountValidTill())
                .createdBy(requestDTO.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .build();

        // Save product to get ID
        Product savedProduct = productRepository.save(product);

        // Generate productName: "{CategoryName} {MaterialTypeName} {PurityName} #{productId}"
        String generatedProductName = String.format("%s %s %s #%d",
                category.getName(),
                materialType.getName(),
                purity.getPurityName(),
                savedProduct.getProductId());

        // Update product with generated productName
        savedProduct.setProductName(generatedProductName);
        productRepository.save(savedProduct);

        return productMapper.mapToProduct(messageSourceUtil.getMessage("product.added"));
    }

    @Override
    @Transactional
    public AddProductResponseDTO updateProduct(UpdateProductRequestDTO requestDTO) {
        // Find existing product
        Product existing = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Validate category exists
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Validate material type exists
        MaterialType materialType = materialTypeRepository.findById(requestDTO.getMaterialTypeId())
                .orElseThrow(() -> new RuntimeException("Material type not found"));

        // Validate purity exists
        Purity purity = purityRepository.findById(requestDTO.getPurityId())
                .orElseThrow(() -> new RuntimeException("Purity not found"));

        // Validate purity belongs to selected material type
        if (!Objects.equals(purity.getMaterialType().getMaterialTypeId(), materialType.getMaterialTypeId())) {
            throw new RuntimeException("Purity does not belong to selected material type");
        }

        // Update product fields
        existing.setCategory(category);
        existing.setMaterialType(materialType);
        existing.setPurity(purity);
        existing.setMakingChargeType("PERCENTAGE"); // As per user, only PERCENTAGE for now
        existing.setMakingChargeValue(requestDTO.getMakingChargeValue());
        existing.setDescription(requestDTO.getDescription());
        existing.setHallmarkCertified(requestDTO.getHallmarkCertified());
        existing.setCertificationNumber(requestDTO.getCertificationNumber());
        existing.setDiscountType(requestDTO.getDiscountType());
        existing.setDiscountValue(requestDTO.getDiscountValue());
        existing.setDiscountValidTill(requestDTO.getDiscountValidTill());
        existing.setUpdatedBy(requestDTO.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());

        // Regenerate productName with updated values
        String generatedProductName = String.format("%s %s %s #%d",
                category.getName(),
                materialType.getName(),
                purity.getPurityName(),
                existing.getProductId());

        existing.setProductName(generatedProductName);

        productRepository.save(existing);

        return productMapper.mapToProduct(messageSourceUtil.getMessage("product.updated"));
    }

    @Override
    @Transactional
    public AddProductResponseDTO deleteProduct(GetProductByIdRequestDTO requestDTO) {
        Product existing = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // Soft delete: set status to INACTIVE
        existing.setStatus("INACTIVE");
        existing.setUpdatedAt(LocalDateTime.now());
        // Note: updatedBy is not provided in GetProductByIdRequestDTO, so we cannot set it.
        // We'll leave it as is, or we could set it to a system user? But the DTO doesn't have it.
        // Since the user said to use GetProductByIdRequestDTO for delete, which only has productId,
        // we cannot set updatedBy. We'll leave it null or as is.
        productRepository.save(existing);
        return productMapper.mapToProduct(messageSourceUtil.getMessage("product.deleted"));
    }

    @Override
    public GetProductResponseDTO getProductById(GetProductByIdRequestDTO requestDTO) {
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return GetProductResponseDTO.fromEntity(product);
    }

    @Override
    public Page<GetProductResponseDTO> getAllProducts(
            int page,
            int size,
            String category,
            String materialType,
            String status,
            Pageable pageable
    ) {

        Specification<Product> specification = Specification.where(null);

        // Category filter
        if (category != null && !category.trim().isEmpty()) {
            String categoryName = category.trim();

            specification = specification.and((root, query, cb) ->
                    cb.equal(
                            cb.lower(root.get("category").get("name")),
                            categoryName.toLowerCase()
                    )
            );
        }

        // Material Type filter
        if (materialType != null && !materialType.trim().isEmpty()) {
            String materialTypeName = materialType.trim();

            specification = specification.and((root, query, cb) ->
                    cb.equal(
                            cb.lower(root.get("materialType").get("name")),
                            materialTypeName.toLowerCase()
                    )
            );
        }

        // Status filter
        if (status != null && !status.trim().isEmpty()) {
            String productStatus = status.trim();

            specification = specification.and((root, query, cb) ->
                    cb.equal(
                            cb.lower(root.get("status")),
                            productStatus.toLowerCase()
                    )
            );
        }

        Page<Product> productPage =
                productRepository.findAll(specification, pageable);

        return productPage.map(GetProductResponseDTO::fromEntity);
    }

}