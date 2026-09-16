package com.shyam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dto.request.AddCategoryRequestDTO;
import com.shyam.dto.request.GetCategoryByIdRequestDTO;
import com.shyam.dto.request.UpdateCategoryRequestDTO;
import com.shyam.dto.response.AddCategoryResponseDTO;
import com.shyam.dto.response.GetCategoriesResponseDTO;
import com.shyam.dto.response.UpdateCategoryResponseDTO;
import com.shyam.service.CategoryService;
import com.shyam.service.Imp.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth/api/v1/admin/category")
@Tag(name = "Admin Category", description = "Admin category management endpoints")
public class AdminCategoryController {

  private final CategoryService categoryService;
  private final CloudinaryService cloudinaryService;
  private final Validator validator;

  @Operation(
      summary = "Get all categories",
      description = "Retrieve a paginated list of all categories.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<Page<GetCategoriesResponseDTO>> getAllCategories(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    log.debug("Entering getAllCategories method with page: {}, size: {}", page, size);
    Page<GetCategoriesResponseDTO> categories = categoryService.getAllCategories(page, size);
    log.info("Successfully retrieved all categories, page: {}, size: {}", page, size);
    log.debug("Exiting getAllCategories method");
    return new BaseResponseDTO<>(categories, null);
  }

  @Operation(summary = "Add category", description = "Add a new category.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Category added successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Validation failed or duplicate name",
        content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PostMapping(value = "/addCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<AddCategoryResponseDTO> addCategories(
      @RequestParam("image") MultipartFile image,
      @RequestParam("data") String addCategoryRequestDTOJson)
      throws JsonProcessingException {
    log.debug("Entering addCategories method");
    log.info("Received request for adding category");

    if (image == null || image.isEmpty()) {
      log.error("Category image is empty");
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          ErrorCodeConstants.ERROR_CODE_VALIDATION,
          "Category image is required.",
          "Category image is empty");
    }

    String imageUrl = cloudinaryService.upload(image);
    log.debug("Image uploaded to Cloudinary, url: {}", imageUrl);

    ObjectMapper mapper = new ObjectMapper();
    AddCategoryRequestDTO addCategoryRequestDTO =
        mapper.readValue(addCategoryRequestDTOJson, AddCategoryRequestDTO.class);
    addCategoryRequestDTO.setImageUrl(imageUrl);

    validateOrThrow(addCategoryRequestDTO);

    var response = categoryService.addCategories(addCategoryRequestDTO);
    log.info("Category added successfully with imageUrl: {}", imageUrl);
    log.debug("Exiting addCategories method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Update category", description = "Update an existing category.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Category updated successfully",
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
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PutMapping(value = "/updateCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<UpdateCategoryResponseDTO> updateCategories(
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestParam("data") String updateCategoryRequestDTOJson)
      throws JsonProcessingException {
    log.debug("Entering updateCategories method");
    log.info("Received request for updating category");

    ObjectMapper mapper = new ObjectMapper();
    UpdateCategoryRequestDTO updateCategoryRequestDTO =
        mapper.readValue(updateCategoryRequestDTOJson, UpdateCategoryRequestDTO.class);

    if (image != null && !image.isEmpty()) {
      String imageUrl = cloudinaryService.upload(image);
      updateCategoryRequestDTO.setImageUrl(imageUrl);
      log.debug("Image uploaded to Cloudinary, url: {}", imageUrl);
    }

    validateOrThrow(updateCategoryRequestDTO);

    var response = categoryService.updateCategoryRequestDTO(updateCategoryRequestDTO);
    log.info("Category updated successfully with id: {}", updateCategoryRequestDTO.getId());
    log.debug("Exiting updateCategories method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Delete category", description = "Delete a category by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Category deleted successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @DeleteMapping("/{categoryId}")
  public BaseResponseDTO<UpdateCategoryResponseDTO> deleteCategory(@PathVariable Long categoryId) {
    log.debug("Entering deleteCategory method with categoryId: {}", categoryId);
    log.info("Received request for deleting category with id: {}", categoryId);
    GetCategoryByIdRequestDTO requestDTO = new GetCategoryByIdRequestDTO();
    requestDTO.setId(categoryId);
    var response = categoryService.deleteCategory(requestDTO);
    log.info("Category deleted successfully with id: {}", categoryId);
    log.debug("Exiting deleteCategory method");
    return new BaseResponseDTO<>(response, null);
  }

  // ===== Common validation helper (SYMException consistent throughout) =====
  private <T> void validateOrThrow(T dto) {
    Set<ConstraintViolation<T>> violations = validator.validate(dto);
    if (!violations.isEmpty()) {
      String errorMessage =
          violations.stream()
              .map(v -> v.getPropertyPath() + " " + v.getMessage())
              .collect(Collectors.joining(", "));
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          ErrorCodeConstants.ERROR_CODE_VALIDATION,
          "Validation failed: " + errorMessage,
          errorMessage);
    }
  }
}
