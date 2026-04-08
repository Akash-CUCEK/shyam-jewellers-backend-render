package com.shyam.mapper;

import com.shyam.dao.CategoryDAO;
import com.shyam.dto.request.AddCategoryRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.entity.Category;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryMapper {

  private final CategoryDAO categoryDAO;

  public static GetCategoryByIdResponseDTO getCategory(Category category) {
    return GetCategoryByIdResponseDTO.builder()
        .categoryId(category.getCategoryId())
        .name(category.getName())
        .imageUrl(category.getImageUrl())
        .showOnHome(String.valueOf(category.getShowOnHome()))
        .createdAt(category.getCreatedAt())
        .createdBy(category.getCreatedBy())
        .updatedAt(category.getUpdatedAt())
        .updatedBy(category.getUpdatedBy())
        .status(category.getStatus())
        .build();
  }

  public static UpdateCategoryResponseDTO mapToDeleteCategoryInMessage(String message) {
    return UpdateCategoryResponseDTO.builder().response(message).build();
  }

  public static Category uploadExcel(String name, boolean status, String createdBy) {
    Category category = new Category();
    category.setName(name);
    category.setStatus(status);
    category.setCreatedAt(LocalDateTime.now());
    category.setCreatedBy(createdBy);
    return category;
  }

  public GetCategoryUserResponseDTO toUserDto(Category category) {
    return GetCategoryUserResponseDTO.builder()
        .categoryId(category.getCategoryId())
        .name(category.getName())
        .imageUrl(category.getImageUrl())
        .showOnHome(category.getShowOnHome())
        .build();
  }

  public GetCategoryResponseDTO getAllCategories() {
    log.debug("Started to get all category");
    List<Category> categories = categoryDAO.findAllCategory();

    List<GetCategoriesResponseDTO> categoryDTOs =
        categories.stream()
            .map(
                category -> {
                  return GetCategoriesResponseDTO.builder()
                      .categoryId(category.getCategoryId())
                      .name(category.getName())
                      .showOnHome(category.getShowOnHome())
                      .createdAt(category.getCreatedAt())
                      .createdBy(category.getCreatedBy())
                      .updatedAt(category.getUpdatedAt())
                      .updatedBy(category.getUpdatedBy())
                      .status(category.getStatus())
                      .build();
                })
            .collect(Collectors.toList());

    GetCategoryResponseDTO responseDTO = new GetCategoryResponseDTO();
    responseDTO.setGetCategoriesResponseDTO(categoryDTOs);
    return responseDTO;
  }

  public static Category addCategories(AddCategoryRequestDTO addCategoryRequestDTO) {
    log.debug("Started to save new category");
    var category = new Category();
    category.setName(addCategoryRequestDTO.getName());
    category.setStatus(addCategoryRequestDTO.getStatus());
    category.setImageUrl(addCategoryRequestDTO.getImageUrl());
    category.setShowOnHome(addCategoryRequestDTO.getShowOnHome());
    category.setCreatedAt(LocalDateTime.now());
    category.setUpdatedAt(LocalDateTime.now());
    category.setCreatedBy(addCategoryRequestDTO.getCreatedBy());
    category.setUpdatedBy(addCategoryRequestDTO.getCreatedBy());
    log.info("new category : {}", category);
    return category;
  }

  public AddCategoryResponseDTO mapToAddCategoryInMessage(String message) {
    return AddCategoryResponseDTO.builder().message(message).build();
  }

  public UpdateCategoryResponseDTO mapToUpdateCategoryInMessage(String message) {
    return UpdateCategoryResponseDTO.builder().response(message).build();
  }

  public GetCategoriesResponseDTO toGetCategoryResponseDTO(Category category) {
    return GetCategoriesResponseDTO.builder()
        .categoryId(category.getCategoryId())
        .name(category.getName())
        .showOnHome(category.getShowOnHome())
        .createdAt(category.getCreatedAt())
        .createdBy(category.getCreatedBy())
        .updatedAt(category.getUpdatedAt())
        .updatedBy(category.getUpdatedBy())
        .status(category.getStatus())
        .build();
  }
}
