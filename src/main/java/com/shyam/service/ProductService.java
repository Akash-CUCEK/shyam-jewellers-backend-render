package com.shyam.service;

import com.shyam.common.constants.ProductStatus;
import com.shyam.dto.request.*;
import com.shyam.dto.response.AddProductResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
  AddProductResponseDTO addProduct(
          AddProductRequestDTO requestDTO,
          List<MultipartFile> images
  );
  AddProductResponseDTO updateProduct(UpdateProductRequestDTO requestDTO);

  AddProductResponseDTO deleteProduct(DeleteProductRequestDTO requestDTO);

  GetProductResponseDTO getProductById(GetProductByIdRequestDTO requestDTO);

  Page<GetProductResponseDTO> getAllProducts(
      Long categoryId, Long materialTypeId, ProductStatus status, Pageable pageable);
}
