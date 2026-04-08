package com.shyam.mapper;

import com.shyam.common.constants.Status;
import com.shyam.dto.request.CreateRepairRequestDTO;
import com.shyam.dto.request.EditRepairRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.entity.RepairService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class RepairRequestMapper {
  public static GetAllRepairResponseDTO getAllRepairRequests(List<RepairService> repairRequest) {
    List<RepairRequestResponseDTO> listRequest =
        repairRequest.stream()
            .map(
                service ->
                    RepairRequestResponseDTO.builder()
                        .serviceId(service.getServiceId())
                        .name(service.getName())
                        .mobileNumber(service.getMobileNumber())
                        .createdAt(service.getCreatedAt())
                        .status(service.getStatus())
                        .build())
            .toList();

    return GetAllRepairResponseDTO.builder().getAllServices(listRequest).build();
  }

  public static RepairRequestResponseDTO getRepairRequestById(RepairService service) {
    return RepairRequestResponseDTO.builder()
        .serviceId(service.getServiceId())
        .notes(service.getNotes())
        .address(service.getAddress())
        .name(service.getName())
        .mobileNumber(service.getMobileNumber())
        .createdAt(service.getCreatedAt())
        .status(service.getStatus())
        .build();
  }

  public static RepairService createRepairRequest(CreateRepairRequestDTO createRepairRequestDTO) {
    var newService = new RepairService();
    newService.setName(createRepairRequestDTO.getName());
    newService.setAddress(createRepairRequestDTO.getAddress());
    newService.setMobileNumber(createRepairRequestDTO.getMobileNumber());
    newService.setCreatedBy(createRepairRequestDTO.getEmail());
    newService.setCreatedAt(LocalDateTime.now());
    newService.setStatus(Status.REQUESTED);
    newService.setNotes(createRepairRequestDTO.getNotes());
    newService.setCreatedBy(createRepairRequestDTO.getEmail());
    return newService;
  }

  public static CreateRepairResponseDTO maoToCreatRepairRequestResponseDTO(String message) {
    return CreateRepairResponseDTO.builder().response(message).build();
  }

  public static RepairService editRepairRequest(
      RepairService service, EditRepairRequestDTO editRepairRequestDTO) {
    service.setName(editRepairRequestDTO.getName());
    service.setAddress(editRepairRequestDTO.getAddress());
    service.setMobileNumber(editRepairRequestDTO.getMobileNumber());
    service.setUpdatedBy(editRepairRequestDTO.getEmail());
    service.setUpdatedAt(LocalDateTime.now());
    service.setStatus(editRepairRequestDTO.getStatus());
    service.setNotes(editRepairRequestDTO.getNotes());
    service.setUpdatedBy(editRepairRequestDTO.getEmail());
    service.setUpdatedAt(LocalDateTime.now());
    return service;
  }

  public static EditRepairResponseDTO mapTOEditRepairRequestResponseDTO(String message) {
    return EditRepairResponseDTO.builder().response(message).build();
  }

  public static DeleteRepairResponseDTO mapTODeleteRepairRequestResponseDTO(String message) {
    return DeleteRepairResponseDTO.builder().response(message).build();
  }
}
