package com.shyam.dao;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.entity.Category;
import com.shyam.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryDAO {
  private final CategoryRepository categoryRepository;

  public List<Category> findAllCategory() {
    try {
      log.debug("Finding the category");
      return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
    } catch (Exception e) {
      log.error("Error while finding category", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to find category"),
          e.getMessage());
    }
  }

  public void saveCategory(Category category) {
    try {
      log.debug("Saving the category");
      categoryRepository.save(category);
    } catch (Exception e) {
      log.error("Error while saving offer Photo", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to save category photo"),
          e.getMessage());
    }
  }

  public Category findByName(String name) {
    return categoryRepository
        .findByName(name)
        .orElseThrow(
            () ->
                new SYMException(
                    HttpStatus.NOT_FOUND,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND_BY_MAIL,
                    String.format("No category found with the provided details."),
                    String.format("No category found with the provided details.", name)));
  }

  public boolean isNameAvailable(String name) {
    try {
      log.debug("Checking if category name is available: {}", name);
      return !categoryRepository.existsByName(name); // true means name is available
    } catch (Exception e) {
      log.error("Error while checking category name availability", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to check category name availability"),
          e.getMessage());
    }
  }

  public Category findById(Long id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(
            () ->
                new SYMException(
                    HttpStatus.NOT_FOUND,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND_BY_MAIL,
                    String.format("No category found with the provided category Id."),
                    String.format("No category found with id", id)));
  }

  public void deleteCategory(Long id) {
    try {
      categoryRepository.deleteById(id);
    } catch (Exception e) {
      log.error("Error while checking category name availability", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to delete category"),
          e.getMessage());
    }
  }

  public boolean canEnableShowOnHome() {
    return categoryRepository.canEnableShowOnHome();
  }

  public Page<Category> findAllCategoryPage(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }
}
