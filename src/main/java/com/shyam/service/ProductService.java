package com.shyam.service;

import com.shyam.dto.request.AddProductRequestDTO;
import com.shyam.dto.request.GetProductByIdRequestDTO;
import com.shyam.dto.request.UpdateProductRequestDTO;
import com.shyam.dto.response.AddProductResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    AddProductResponseDTO addProduct(AddProductRequestDTO requestDTO);

    AddProductResponseDTO updateProduct(UpdateProductRequestDTO requestDTO);

    AddProductResponseDTO deleteProduct(GetProductByIdRequestDTO requestDTO);

    GetProductResponseDTO getProductById(GetProductByIdRequestDTO requestDTO);

    Page<GetProductResponseDTO> getAllProducts(int page, int size, String category, String materialType, String status, Pageable pageable);
}