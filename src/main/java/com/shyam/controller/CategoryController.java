package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.GetCategoryByIdRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/getAllCategory")
  public BaseResponseDTO<GetAllCategoryUserResponseDTO> getAllCategories() {
    log.info("Received request for getting all category");
    var response = categoryService.getAllCategoriesUser();
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get category by ID",
      description = "Retrieve a specific category by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/getCategory")
  public BaseResponseDTO<GetCategoryUserResponseDTO> getCategory(
      @Valid @RequestBody GetCategoryByIdRequestDTO getCategoryByIdRequestDTO) {
    log.info("Received request for get category by Id");
    var response = categoryService.getCategoryUser(getCategoryByIdRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }
}
