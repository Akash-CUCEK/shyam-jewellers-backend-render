package com.shyam.service;

import com.shyam.dto.request.AddProductRequestDTO;
import com.shyam.dto.request.GetProductByIdRequestDTO;
import com.shyam.dto.request.UpdateProductRequestDTO;
import com.shyam.dto.response.AddProductResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import java.util.List;

public interface ProductService {

  AddProductResponseDTO addProduct(AddProductRequestDTO requestDTO);

  AddProductResponseDTO updateProduct(UpdateProductRequestDTO requestDTO);

  AddProductResponseDTO deleteProduct(GetProductByIdRequestDTO requestDTO);

  GetProductResponseDTO getProductById(GetProductByIdRequestDTO requestDTO);

  List<GetProductResponseDTO> getAllProducts();
}