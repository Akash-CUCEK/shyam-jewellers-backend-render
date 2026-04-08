package com.shyam.mapper;

import com.shyam.common.constants.ServiceType;
import com.shyam.common.constants.Status;
import com.shyam.dto.request.CreateHomeServiceRequestDTO;
import com.shyam.dto.request.EditHomeServiceRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.entity.ServiceHome;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HomeServiceMapper {

  public static GetAllHomeServiceResponseDTO getAllHomeServiceRequests(List<ServiceHome> services) {
    List<HomeServiceResponseDTO> responseList =
        services.stream()
            .map(
                service ->
                    HomeServiceResponseDTO.builder()
                        .serviceId(service.getServiceId())
                        .name(service.getName())
                        .address(service.getAddress())
                        .notes(service.getNotes())
                        .phoneNumber(service.getPhoneNumber())
                        .serviceType(service.getServiceType())
                        .createdAt(service.getCreatedAt())
                        .status(service.getStatus())
                        .build())
            .toList();

    return GetAllHomeServiceResponseDTO.builder().getAllServices(responseList).build();
  }

  public static HomeServiceResponseDTO getHomeServiceRequestById(ServiceHome service) {
    return HomeServiceResponseDTO.builder()
        .serviceId(service.getServiceId())
        .name(service.getName())
        .phoneNumber(service.getPhoneNumber())
        .serviceType(service.getServiceType())
        .createdAt(service.getCreatedAt())
        .status(service.getStatus())
        .build();
  }

  public static ServiceHome createHomeServiceRequests(CreateHomeServiceRequestDTO dto) {
    ServiceHome service = new ServiceHome();
    service.setName(dto.getName());
    service.setAddress(dto.getAddress());
    service.setPhoneNumber(dto.getPhoneNumber());
    service.setServiceType(ServiceType.valueOf(dto.getServiceType()));
    service.setStatus(Status.REQUESTED);
    service.setNotes(dto.getNotes());
    service.setCreatedBy(dto.getEmail());
    service.setCreatedAt(LocalDateTime.now());

    return service;
  }

  public static ServiceHome editHomeServiceRequest(
      ServiceHome service, EditHomeServiceRequestDTO dto) {
    service.setName(dto.getName());
    service.setAddress(dto.getAddress());
    service.setStatus(dto.getStatus() != null ? dto.getStatus() : service.getStatus());
    service.setPhoneNumber(dto.getPhoneNumber());
    service.setNotes(dto.getNotes());
    service.setServiceType(dto.getServiceType());
    service.setUpdatedBy(dto.getUpdatedBy());
    service.setUpdatedAt(LocalDateTime.now());
    return service;
  }

  public static EditHomeServiceResponseDTO mapTOEditHomeServiceResponseDTO(String message) {
    return EditHomeServiceResponseDTO.builder().Response(message).build();
  }

  public static DeleteHomeServiceResponseDTO mapTODeleteHomeServiceResponseDTO(String message) {
    return DeleteHomeServiceResponseDTO.builder().Response(message).build();
  }

  public static CreateHomeServiceResponseDTO maoToCreateHomeServiceResponseDTO(String message) {
    return CreateHomeServiceResponseDTO.builder().Response(message).build();
  }
}
