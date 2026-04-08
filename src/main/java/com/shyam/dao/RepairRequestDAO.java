package com.shyam.dao;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.entity.RepairService;
import com.shyam.repository.RepairRequestRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RepairRequestDAO {
  private final RepairRequestRepository repairRequestRepository;

  public List<RepairService> findAllRepairRequest() {
    try {
      List<RepairService> services = repairRequestRepository.findAllByOrderByCreatedAtDesc();
      if (services == null || services.isEmpty()) {
        return Collections.emptyList();
      }
      return services;
    } catch (Exception e) {
      log.error("Error while fetching all repair requests", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to fetch repair requests",
          e.getMessage());
    }
  }

  public RepairService findRepairRequest(Long serviceId) {
    return repairRequestRepository
        .findById(serviceId)
        .orElseThrow(
            () ->
                new SYMException(
                    HttpStatus.NOT_FOUND,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
                    String.format(
                        "No Repair Service found with the provided serviceId: %d", serviceId),
                    String.format("Repair Service with ID %d not found", serviceId)));
  }

  public void saveRepairRequest(RepairService service) {
    try {
      log.debug("Saving the repair service: {}", service.getServiceId());
      repairRequestRepository.save(service);
    } catch (Exception e) {
      log.error("Error while saving repair service: {}", service.getServiceId(), e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to save repair service",
          e.getMessage());
    }
  }

  public void deleteRepairService(Long serviceId) {
    try {
      log.debug("Deleting the repair service: {}", serviceId);
      repairRequestRepository.deleteById(serviceId);
    } catch (Exception e) {
      log.error("Error while deleting repair service: {}", serviceId, e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to delete repair service with serviceId: %d", serviceId),
          e.getMessage());
    }
  }

  public List<RepairService> searchRepairRequests(String keyword) {
    Long serviceId = null;
    String name = null;

    if (keyword != null && keyword.matches("\\d+")) {
      // Agar sirf number hai toh serviceId treat karo
      serviceId = Long.valueOf(keyword);
    } else {
      // Warna name treat karo
      name = keyword;
    }
    return repairRequestRepository.searchRepairRequests(serviceId, name);
  }
}
