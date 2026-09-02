package com.shyam.service;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.AddCategoryRequestDTO;
import com.shyam.dto.request.GetCategoryByIdRequestDTO;
import com.shyam.dto.request.UpdateCategoryRequestDTO;
import com.shyam.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface CategoryService {

  Page<BaseResponseDTO<GetCategoriesResponseDTO>> getAllCategories(int page, int size);

  AddCategoryResponseDTO addCategories(AddCategoryRequestDTO addCategoryRequestDTO);

  UpdateCategoryResponseDTO updateCategoryRequestDTO(
          UpdateCategoryRequestDTO updateCategoryRequestDTO);

  UpdateCategoryResponseDTO deleteCategory(GetCategoryByIdRequestDTO updateCategoryRequestDTO);

  ResponseEntity<?> uploadExcel(MultipartFile file, String createdBy);

  GetAllCategoryUserResponseDTO getAllCategoriesUser();

  GetCategoryUserResponseDTO getCategoryUser(GetCategoryByIdRequestDTO getCategoryByIdRequestDTO);
}
