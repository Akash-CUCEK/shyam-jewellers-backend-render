package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.GetCategoryByIdRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/public")
@Tag(name = "Category", description = "Category management endpoints")
public class CategoryController {

  private final CategoryService categoryService;

  @Operation(summary = "Get all categories", description = "Retrieve a list of all categories.")
  @PostMapping("/getAllCategory")
  public BaseResponseDTO<GetAllCategoryUserResponseDTO> getAllCategories() {
    log.info("Received request for getting all category");
    var response = categoryService.getAllCategoriesUser();
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get category by ID",
      description = "Retrieve a specific category by its ID.")
  @PostMapping("/getCategory")
  public BaseResponseDTO<GetCategoryUserResponseDTO> getCategory(
      @RequestBody GetCategoryByIdRequestDTO getCategoryByIdRequestDTO) {
    log.info("Received request for get category by Id");
    var response = categoryService.getCategoryUser(getCategoryByIdRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }
}
