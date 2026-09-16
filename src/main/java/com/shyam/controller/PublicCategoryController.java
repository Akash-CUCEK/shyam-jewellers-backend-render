package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.GetCategoryByIdRequestDTO;
import com.shyam.dto.response.GetAllCategoryUserResponseDTO;
import com.shyam.dto.response.GetCategoryUserResponseDTO;
import com.shyam.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Category", description = "Public category endpoints")
public class PublicCategoryController {

  private final CategoryService categoryService;

  @Operation(
      summary = "Get all categories (public)",
      description = "Retrieve a list of all active categories.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping
  public BaseResponseDTO<List<GetCategoryUserResponseDTO>> getAllCategoriesUser() {
    log.debug("Entering getAllCategoriesUser method");
    log.info("Received request for getting all categories (public)");
    GetAllCategoryUserResponseDTO response = categoryService.getAllCategoriesUser();
    log.info("Successfully retrieved all active categories (public)");
    log.debug("Exiting getAllCategoriesUser method");
    return new BaseResponseDTO<>(response.getCategories(), null);
  }

  @Operation(
      summary = "Get category by ID (public)",
      description = "Retrieve a specific active category by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/{categoryId}")
  public BaseResponseDTO<GetCategoryUserResponseDTO> getCategoryUser(
      @PathVariable Long categoryId) {
    log.debug("Entering getCategoryUser method with categoryId: {}", categoryId);
    log.info("Received request for getting category by id: {}", categoryId);
    GetCategoryByIdRequestDTO requestDTO = new GetCategoryByIdRequestDTO();
    requestDTO.setId(categoryId);
    GetCategoryUserResponseDTO response = categoryService.getCategoryUser(requestDTO);
    log.info("Successfully retrieved category by id: {}", categoryId);
    log.debug("Exiting getCategoryUser method");
    return new BaseResponseDTO<>(response, null);
  }
}
