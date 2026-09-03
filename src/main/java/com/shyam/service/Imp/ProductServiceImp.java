package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.*;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dao.ProductDAO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.entity.Category;
import com.shyam.entity.Products;
import com.shyam.mapper.ProductMapper;
import com.shyam.repository.CategoryRepository;
import com.shyam.service.ProductService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImp implements ProductService {
  private final ProductDAO productDAO;
  private final ProductMapper productMapper;
  private final MessageSourceUtil messageSourceUtil;
  private final CloudinaryService cloudinaryService;
  private final CategoryRepository categoryRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<BaseResponseDTO<GetAllProductsResponseDTO>> getAllProducts(int page, int size) {

    Pageable pageable =
        PageRequest.of(page, size, Sort.by(Sort.Order.desc("updatedAt").nullsLast()));
    Page<Products> productPage = productDAO.getAllProduct(pageable);

    return productPage.map(
        product -> new BaseResponseDTO<>(productMapper.toGetAllProductsResponse(product), null));
  }

  @Override
  @jakarta.transaction.Transactional
  public ProductAddResponseDTO addProduct(ProductAddRequestDTO dto, MultipartFile image) {

    log.info("Uploading image to Cloudinary...");

    var imageUrl = cloudinaryService.upload(image);
    Category category =
        categoryRepository
            .findByName(dto.getCategory())
            .orElseThrow(() -> new RuntimeException("Category not found"));

    Products product = productMapper.toEntity(dto, category);
    product.setImageUrl(imageUrl);

    Products saved = productDAO.save(product);

    return productMapper.toAddProductResponse(
        saved, messageSourceUtil.getMessage(MESSAGE_CODE_PRODUCT_ADDED));
  }

  @Override
  @jakarta.transaction.Transactional
  public UpdateResponseDTO updateProduct(UpdateRequestDTO dto) {

    // 🔍 Product fetch
    Products product = productDAO.findProduct(dto.getName());

    if (product == null) {
      throw new RuntimeException("Product not found");
    }

    // 💾 Save
    productDAO.save(product);

    // 📤 Response
    return UpdateResponseDTO.builder().message("Product updated successfully").build();
  }

  @Override
  @jakarta.transaction.Transactional
  public DeleteResponseDTO deleteProduct(DeleteProductRequestDTO dto) {
    log.info("Processing to delete product");
    Products product = productDAO.findProduct(dto.getName());
    productDAO.delete(product);
    return productMapper.toDeleteProductResponse(
        messageSourceUtil.getMessage(MESSAGE_CODE_PRODUCT_DELETED));
  }

  
  private PageResponseDTO<AllProductResponseDTO> buildPageResponse(Page<Products> page) {
    return new PageResponseDTO<>(
        page.getContent().stream().map(productMapper::toAllProductResponse).toList(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isLast());
  }

  @Override
  @Transactional(readOnly = true)
  public GenderResponseDTO getGenderProduct(GenderRequestDTO dto) {
    log.info("Processing to get product based on gender");
    return productMapper.toGenderResponse(productDAO.getGenderProduct(dto.getGender()));
  }

  //  @Override
  //  public ProductResponseDTO getNameProduct(GetProductByNameRequestDTO dto) {
  //    log.info("Processing to get product based on name");
  ////    return productMapper.toProductResponse(productDAO.findProduct(dto.getName()));
  //  }

  @Override
  @Transactional(readOnly = true)
  public PageResponseDTO<AllProductResponseDTO> getProductsByCategory(
      String category, Pageable pageable) {
    log.info("Processing to get product based on category");

    Page<Products> page = productDAO.getProductsByCategory(category.toUpperCase(), pageable);

    if (page.isEmpty()) {
      throw new SYMException(
          HttpStatus.NOT_FOUND,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_PRODUCT_NOT_FOUND,
          "No products found for category: " + category,
          "No products in DB for category: " + category);
    }

    return new PageResponseDTO<>(
        page.getContent().stream().map(productMapper::toAllProductResponse).toList(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isLast());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AllProductResponseDTO> getAllProduct(Pageable pageable) {
    log.info("Processing to get all product");
    return productDAO.getAllProduct(pageable).map(productMapper::toAllProductResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public AllProductResponseDTO getProductById(Long productId) {
    Products product =
        productDAO
            .getProductById(productId)
            .orElseThrow(
                () ->
                    new SYMException(
                        HttpStatus.NOT_FOUND,
                        SYMErrorType.GENERIC_EXCEPTION,
                        ErrorCodeConstants.ERROR_CODE_PRODUCT_NOT_FOUND,
                        "No product found for productId: " + productId,
                        "No product found for productId: " + productId));

    return productMapper.toAllProductResponse(product);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponseDTO<AllProductResponseDTO> getByMaterialType(
      String materialType, Pageable pageable) {
    log.info("Processing to get all products based on material type");
    Page<Products> page = productDAO.getProductsByMaterialType(materialType, pageable);

    if (page.isEmpty()) {
      throw new SYMException(
          HttpStatus.NOT_FOUND,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_PRODUCT_NOT_FOUND,
          "No products found for material Type: " + materialType,
          "No products in DB for material Type: " + materialType);
    }

    return new PageResponseDTO<>(
        page.getContent().stream().map(productMapper::toAllProductResponse).toList(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isLast());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AllProductResponseDTO> getFilteredProducts(
      ProductFilterRequestDTO dto, Pageable pageable) {
    log.info("Processing to get filter product");
    return productDAO.findProductsByFilters(dto, pageable).map(productMapper::toAllProductResponse);
  }
}
