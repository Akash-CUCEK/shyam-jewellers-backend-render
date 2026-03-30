//package com.shyam.service;
//
//import com.shyam.dto.request.*;
//import com.shyam.dto.response.*;
//import jakarta.validation.Valid;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.math.BigDecimal;
//
//public interface ProductService {
//
//    ProductAddResponseDTO addProduct(
//            @Valid ProductAddRequestDTO productAddRequestDTO
//    );
//
//    UpdateResponseDTO updateProduct(
//            @Valid UpdateRequestDTO updateRequestDTO
//    );
//
//    DeleteResponseDTO deleteProduct(
//            @Valid DeleteProductRequestDTO deleteProductRequestDTO
//    );
//
//    PageResponseDTO<AllProductResponseDTO> getProductsUnderPrice(
//            BigDecimal price,Pageable pageable
//    );
//
//    GenderResponseDTO getGenderProduct(
//            @Valid GenderRequestDTO genderRequestDTO
//    );
//
//    ProductResponseDTO getNameProduct(
//            @Valid GetProductByNameRequestDTO getProductByNameRequestDTO
//    );
//
//    PageResponseDTO<AllProductResponseDTO> getProductsByCategory(
//            String category,
//            Pageable pageable
//    );
//
//    Page<AllProductResponseDTO> getAllProduct(
//            Pageable pageable
//    );
//
//    Page<AllProductResponseDTO> getFilteredProducts(
//            @Valid ProductFilterRequestDTO filterDTO,
//            Pageable pageable
//    );
//
//    AllProductResponseDTO getProductById(Long productId);
//
//    PageResponseDTO<AllProductResponseDTO> getByMaterialType(String materialType, Pageable pageable);
//
//    PageResponseDTO<AllProductResponseDTO> getProductsByAbovePrice(BigDecimal price, Pageable pageable);
//}
