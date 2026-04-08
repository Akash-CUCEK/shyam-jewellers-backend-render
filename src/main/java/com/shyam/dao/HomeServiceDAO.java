package com.shyam.dao;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.entity.ServiceHome;
import com.shyam.repository.HomeServiceRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HomeServiceDAO {
  private final HomeServiceRepository homeServiceRepository;

  public List<ServiceHome> searchHomeServices(String keyword) {
    Long serviceId = null;
    String name = null;

    if (keyword != null && keyword.matches("\\d+")) {
      // Agar sirf number hai toh serviceId treat karo
      serviceId = Long.valueOf(keyword);
    } else {
      // Warna name treat karo
      name = keyword;
    }
    return homeServiceRepository.searchHomeServices(serviceId, name);
  }

  public void saveHomeService(ServiceHome service) {
    try {
      log.debug("Saving the Home Service");
      homeServiceRepository.save(service);
    } catch (Exception e) {
      log.error("Error while saving home service", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to save home service",
          e.getMessage());
    }
  }

  public void deleteHomeService(Long serviceId) {
    try {
      log.debug("Deleting the home service: {}", serviceId);
      homeServiceRepository.deleteById(serviceId);
    } catch (Exception e) {
      log.error("Error while deleting home service: {}", serviceId, e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to delete home service with serviceId:  %s", serviceId),
          e.getMessage());
    }
  }

  public List<ServiceHome> getAllHomeServiceRequests() {
    try {
      log.info("found services");
      List<ServiceHome> services = homeServiceRepository.findAllByOrderByCreatedAtDesc();
      return services != null ? services : Collections.emptyList();
    } catch (Exception e) {
      log.error("Error while fetching all home service requests", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_PRODUCT_NOT_FOUND,
          "Failed to fetch all home service requests",
          e.getMessage());
    }
  }

  public ServiceHome findHomeService(Long serviceId) {
    return homeServiceRepository
        .findById(serviceId)
        .orElseThrow(
            () ->
                new SYMException(
                    HttpStatus.NOT_FOUND,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND_BY_MAIL,
                    "No Home Service found with the provided serviceId.",
                    String.format(
                        "No Home Service found with the provided serviceId: %d", serviceId)));
  }
}
