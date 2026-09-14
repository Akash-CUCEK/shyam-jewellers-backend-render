package com.shyam.service.Imp;

import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dto.request.AddProductRequestDTO;
import com.shyam.dto.request.GetProductByIdRequestDTO;
import com.shyam.dto.request.UpdateProductRequestDTO;
import com.shyam.dto.response.AddProductResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import com.shyam.entity.Purity;
import com.shyam.entity.Product;
import com.shyam.mapper.ProductMapper;
import com.shyam.repository.CategoryRepository;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.ProductRepository;
import com.shyam.repository.PurityRepository;
import com.shyam.service.ProductService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
    // Validate that the purity's materialType matches the given materialType
    Purity purity = purityRepository.findById(requestDTO.getPurityId())
        .orElseThrow(() -> new RuntimeException("Purity not found"));
    if (!purity.getMaterialType().getMaterialTypeId().equals(requestDTO.getMaterialTypeId())) {
      throw new RuntimeException("Purity's material type does not match the given material type");
    }

    // Create product entity without productName
    Product product =
        Product.builder()
            .category(categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")))
            .materialType(materialTypeRepository.findById(requestDTO.getMaterialTypeId())
                .orElseThrow(() -> new RuntimeException("Material type not found")))
            .purity(purity)
            .makingChargeType(requestDTO.getMakingChargeType())
            .makingChargeValue(requestDTO.getMakingChargeValue())
            .description(requestDTO.getDescription())
            .status(requestDTO.getStatus())
            .hallmarkCertified(requestDTO.getHallmarkCertified())
            .certificationNumber(requestDTO.getCertificationNumber())
            .discountType(requestDTO.getDiscountType())
            .discountValue(requestDTO.getDiscountValue())
            .discountValidTill(requestDTO.getDiscountValidTill())
            .createdBy(requestDTO.getCreatedBy())
            .createdAt(LocalDateTime.now())
            .status(requestDTO.getStatus()) // note: we set status twice, but okay
            .build();

    // Save to get the productId
    Product saved = productRepository.save(product);

    // Generate productName
    String productName = String.format("%s %s %s #%d",
        saved.getCategory().getName(),
        saved.getMaterialType().getName(),
        saved.getPurity().getPurityName(),
        saved.getProductId());
    saved.setProductName(productName);

    // Save again to update productName
    Product updated = productRepository.save(saved);

    return productMapper.mapToProduct(
        messageSourceUtil.getMessage(MESSAGE_CODE_ADD_PRODUCT));
  }

  @Override
  @Transactional
  public AddProductResponseDTO updateProduct(UpdateProductRequestDTO requestDTO) {
    Product existing =
        productRepository
            .findById(requestDTO.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

    // Validate that the purity's materialType matches the given materialType
    Purity purity = purityRepository.findById(requestDTO.getPurityId())
        .orElseThrow(() -> new RuntimeException("Purity not found"));
    if (!purity.getMaterialType().getMaterialTypeId().equals(requestDTO.getMaterialTypeId())) {
      throw new RuntimeException("Purity's material type does not match the given material type");
    }

    existing.setCategory(categoryRepository.findById(requestDTO.getCategoryId())
        .orElseThrow(() -> new RuntimeException("Category not found")));
    existing.setMaterialType(materialTypeRepository.findById(requestDTO.getMaterialTypeId())
        .orElseThrow(() -> new RuntimeException("Material type not found")));
    existing.setPurity(purity);
    existing.setMakingChargeType(requestDTO.getMakingChargeType());
    existing.setMakingChargeValue(requestDTO.getMakingChargeValue());
    existing.setDescription(requestDTO.getDescription());
    existing.setStatus(requestDTO.getStatus());
    existing.setHallmarkCertified(requestDTO.getHallmarkCertified());
    existing.setCertificationNumber(requestDTO.getCertificationNumber());
    existing.setDiscountType(requestDTO.getDiscountType());
    existing.setDiscountValue(requestDTO.getDiscountValue());
    existing.setDiscountValidTill(requestDTO.getDiscountValidTill());
    existing.setUpdatedBy(requestDTO.getUpdatedBy());
    existing.setUpdatedAt(LocalDateTime.now());

    // Note: productName will be regenerated based on the new values and the same productId
    String productName = String.format("%s %s %s #%d",
        existing.getCategory().getName(),
        existing.getMaterialType().getName(),
        existing.getPurity().getPurityName(),
        existing.getProductId());
    existing.setProductName(productName);

    Product updated = productRepository.save(existing);
    return productMapper.mapToProduct(
        messageSourceUtil.getMessage(MESSAGE_CODE_UPDATE_PRODUCT));
  }

  @Override
  public AddProductResponseDTO deleteProduct(GetProductByIdRequestDTO requestDTO) {
    Product existing =
        productRepository
            .findById(requestDTO.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
    productRepository.delete(existing);
    return productMapper.mapToProduct(
        messageSourceUtil.getMessage(MESSAGE_CODE_DELETE_PRODUCT));
  }

  @Override
  public GetProductResponseDTO getProductById(GetProductByIdRequestDTO requestDTO) {
    Product product =
        productRepository
            .findById(requestDTO.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
    return GetProductResponseDTO.fromEntity(product);
  }

  @Override
  public List<GetProductResponseDTO> getAllProducts() {
    List<Product> products = productRepository.findAll();
    return products.stream()
        .map(GetProductResponseDTO::fromEntity)
        .collect(Collectors.toList());
  }
}