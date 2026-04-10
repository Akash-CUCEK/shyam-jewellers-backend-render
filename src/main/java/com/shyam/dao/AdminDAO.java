package com.shyam.dao;

import com.shyam.common.constants.Role;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.entity.AdminUsers;
import com.shyam.entity.OfferPhoto;
import com.shyam.repository.AdminRepository;
import com.shyam.repository.OfferPhotoRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDAO {

  private static final Logger logger = LoggerFactory.getLogger(AdminDAO.class);
  private final AdminRepository adminRepository;
  private final OfferPhotoRepository offerPhotoRepository;

  public AdminUsers findUserByEmail(String username) {
    try {
      return adminRepository
          .findByEmail(username)
          .orElseThrow(
              () ->
                  new SYMException(
                      HttpStatus.NOT_FOUND,
                      SYMErrorType.GENERIC_EXCEPTION,
                      ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND_BY_MAIL,
                      String.format("No user found with the provided email address."),
                      String.format(
                          "Login attempted with email %s which does not exist", username)));
    } catch (Exception e) {
      logger.error("Error while finding user: {}", username, e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("No User found"),
          e.getMessage());
    }
  }

  public AdminUsers save(AdminUsers adminUsers) {
    try {
      logger.debug("Saving the user: {}", adminUsers.getEmail());
      adminRepository.save(adminUsers);
      return adminUsers;
    } catch (Exception e) {
      logger.error("Error while saving user: {}", adminUsers.getEmail(), e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to save user with email %s", adminUsers.getEmail()),
          e.getMessage());
    }
  }

  public Optional<AdminUsers> findByEmail(String email) {
    return adminRepository.findByEmail(email);
  }

  public void saveOffer(OfferPhoto offer) {
    try {
      logger.debug("Saving the photo");
      offerPhotoRepository.save(offer);
    } catch (Exception e) {
      logger.error("Error while saving offer Photo", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to save offer photo"),
          e.getMessage());
    }
  }

  public OfferPhoto getLatestOfferPhoto() {
    try {
      logger.debug("Fetching latest offer photo");
      return offerPhotoRepository.findTopByOrderByCreatedAtDesc();
    } catch (Exception e) {
      logger.error("Error while fetching latest offer photo", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to fetch latest offer photo",
          e.getMessage());
    }
  }

  public List<AdminUsers> findByRole(Role role) {
    try {
      logger.debug("Fetching users with role: {}", role);
      return adminRepository.findByRole(role);
    } catch (Exception e) {
      logger.error("Error while fetching users by role", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to fetch users with role %s", role),
          e.getMessage());
    }
  }

  public void delete(AdminUsers admin) {
    try {
      logger.debug("Deleting the admin");
      adminRepository.delete(admin);
    } catch (Exception e) {
      logger.error("Error while deleting admin", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to delete admin",
          e.getMessage());
    }
  }
}
