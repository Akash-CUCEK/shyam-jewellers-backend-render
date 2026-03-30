//package com.shyam.service;
//
//import com.shyam.dto.request.AddCategoryRequestDTO;
//import com.shyam.dto.request.GetCategoryByIdRequestDTO;
//import com.shyam.dto.response.*;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.multipart.MultipartFile;
//
//public interface CategoryService {
//    GetCategoryResponseDTO getAllCategories();
//
//    AddCategoryResponseDTO addCategories(AddCategoryRequestDTO addCategoryRequestDTO);
//
//    UpdateCategoryResponseDTO updateCategoryRequestDTO(AddCategoryRequestDTO updateCategoryRequestDTO);
//
//    GetCategoryByIdResponseDTO getCategory(GetCategoryByIdRequestDTO getCategoryByIdRequestDTO);
//
//    UpdateCategoryResponseDTO deleteCategory(GetCategoryByIdRequestDTO updateCategoryRequestDTO);
//
//    ResponseEntity<?> uploadExcel(MultipartFile file, String createdBy);
//
//    GetAllCategoryUserResponseDTO getAllCategoriesUser();
//
//    GetCategoryUserResponseDTO getCategoryUser(GetCategoryByIdRequestDTO getCategoryByIdRequestDTO);
//}
//
//
