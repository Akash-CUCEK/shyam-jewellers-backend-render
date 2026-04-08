package com.shyam.service;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

  ProductAddResponseDTO addProduct(ProductAddRequestDTO productAddRequestDTO, MultipartFile image);

  Page<BaseResponseDTO<GetAllProductsResponseDTO>> getAllProducts(int page, int size);

  UpdateResponseDTO updateProduct(@Valid UpdateRequestDTO updateRequestDTO);

  DeleteResponseDTO deleteProduct(@Valid DeleteProductRequestDTO deleteProductRequestDTO);

  PageResponseDTO<AllProductResponseDTO> getProductsUnderPrice(BigDecimal price, Pageable pageable);

  GenderResponseDTO getGenderProduct(@Valid GenderRequestDTO genderRequestDTO);

//  ProductResponseDTO getNameProduct(@Valid GetProductByNameRequestDTO getProductByNameRequestDTO);

  PageResponseDTO<AllProductResponseDTO> getProductsByCategory(String category, Pageable pageable);

  Page<AllProductResponseDTO> getAllProduct(Pageable pageable);

  Page<AllProductResponseDTO> getFilteredProducts(
      @Valid ProductFilterRequestDTO filterDTO, Pageable pageable);

  AllProductResponseDTO getProductById(Long productId);

  PageResponseDTO<AllProductResponseDTO> getByMaterialType(String materialType, Pageable pageable);

  PageResponseDTO<AllProductResponseDTO> getProductsByAbovePrice(
      BigDecimal price, Pageable pageable);
}
