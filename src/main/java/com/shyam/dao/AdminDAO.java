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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDAO {

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
      log.debug("Saving the user: {}", adminUsers.getEmail());
      adminRepository.save(adminUsers);
      return adminUsers;
    } catch (Exception e) {
      log.error("Error while saving user: {}", adminUsers.getEmail(), e);
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

      log.info("Saving offer photo. Position: {}", offer.getPosition());

      return offerPhotoRepository.save(offer);

    } catch (Exception e) {

      log.error("Error while saving offer photo", e);

      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to save offer photo",
          e.getMessage());
    }
  }

  public OfferPhoto getPhotoByPosition(int position) {

    try {

      log.info("Fetching offer photo for position: {}", position);

      return offerPhotoRepository.findByPosition(position).orElse(null);

    } catch (Exception e) {

      log.error("Error while fetching offer photo for position: {}", position, e);

      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to fetch offer photo",
          e.getMessage());
    }
  }

  public List<OfferPhoto> getPhotosWithPosition() {
    try {
      log.debug("Fetching offer photos with position");

      return offerPhotoRepository.findByPositionBetweenOrderByPosition(1, 5);

    } catch (Exception e) {
      log.error("Error while fetching offer photos", e);

      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to fetch available offer photos",
          e.getMessage());
    }
  }

  public List<AdminUsers> findByRoleIn(List<Role> roles) {
    try {
      log.debug("Fetching users with roles: {}", roles);

      return adminRepository.findByRoleIn(roles);

    } catch (Exception e) {
      log.error("Error while fetching users by roles: {}", roles, e);

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
      log.debug("Deleting the admin");
      adminRepository.delete(admin);
    } catch (Exception e) {
      log.error("Error while deleting admin", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to delete admin",
          e.getMessage());
    }
  }
}
