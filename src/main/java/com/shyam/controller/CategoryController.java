package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.GetCategoryByIdRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/public")
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping("/getAllCategory")
  public BaseResponseDTO<GetAllCategoryUserResponseDTO> getAllCategories() {
    log.info("Received request for getting all category");
    var response = categoryService.getAllCategoriesUser();
    return new BaseResponseDTO<>(response, null);
  }

  @PostMapping("/getCategory")
  public BaseResponseDTO<GetCategoryUserResponseDTO> getCategory(
      @RequestBody GetCategoryByIdRequestDTO getCategoryByIdRequestDTO) {
    log.info("Received request for get category by Id");
    var response = categoryService.getCategoryUser(getCategoryByIdRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }
}
