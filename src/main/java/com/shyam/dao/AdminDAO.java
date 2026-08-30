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
    return adminRepository
        .findByEmail(username)
        .orElseThrow(
            () ->
                new SYMException(
                    HttpStatus.NOT_FOUND,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND_BY_MAIL,
                    "No user found with the provided email address.",
                    "Login attempted with email " + username));
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

  public OfferPhoto saveOffer(OfferPhoto offer) {

    try {

      logger.info(
              "Saving offer photo. Position: {}",
              offer.getPosition()
      );

      return offerPhotoRepository.save(offer);

    } catch (Exception e) {

      logger.error(
              "Error while saving offer photo",
              e
      );

      throw new SYMException(
              HttpStatus.INTERNAL_SERVER_ERROR,
              SYMErrorType.GENERIC_EXCEPTION,
              ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
              "Failed to save offer photo",
              e.getMessage()
      );
    }
  }
  public OfferPhoto getPhotoByPosition(int position) {

    try {

      logger.info(
              "Fetching offer photo for position: {}",
              position
      );

      return offerPhotoRepository
              .findByPosition(position)
              .orElse(null);

    } catch (Exception e) {

      logger.error(
              "Error while fetching offer photo for position: {}",
              position,
              e
      );

      throw new SYMException(
              HttpStatus.INTERNAL_SERVER_ERROR,
              SYMErrorType.GENERIC_EXCEPTION,
              ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
              "Failed to fetch offer photo",
              e.getMessage()
      );
    }
  }
  public List<OfferPhoto> getPhotosWithPosition() {
    try {
      logger.debug("Fetching offer photos with position");

      return offerPhotoRepository
              .findByPositionBetweenOrderByPosition(1, 5);

    } catch (Exception e) {
      logger.error("Error while fetching offer photos", e);

      throw new SYMException(
              HttpStatus.INTERNAL_SERVER_ERROR,
              SYMErrorType.GENERIC_EXCEPTION,
              ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
              "Failed to fetch available offer photos",
              e.getMessage()
      );
    }
  }

  public List<AdminUsers> findByRoleIn(List<Role> roles) {
    try {
      logger.debug("Fetching users with roles: {}", roles);

      return adminRepository.findByRoleIn(roles);

    } catch (Exception e) {
      logger.error("Error while fetching users by roles: {}", roles, e);

      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to fetch users with roles %s", roles),
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
