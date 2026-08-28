package com.shyam.mapper;

import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.entity.AdminUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminMapper {

  public AdminLogoutResponseDTO mapToAdminLogoutInMessage(String message) {
    return AdminLogoutResponseDTO.builder().message(message).build();
  }

  public EditAdminResponseDTO mapToAdminEditInMessage(String message) {
    return EditAdminResponseDTO.builder().response(message).build();
  }

  public RegisterResponseDTO mapToRegisterAdminInMessage(String message) {
    return RegisterResponseDTO.builder().response(message).build();
  }

  public void offerUpdate(EditPhotoRequestDTO editPhotoRequestDTO) {}

  public EditPhotoResponseDTO mapToEditPhotoRequestDTOAdminInMessage(String message) {
    return EditPhotoResponseDTO.builder().response(message).build();
  }

  public DeleteAdminResponseDTO mapToDeleteAdminInMessage(String message) {
    return DeleteAdminResponseDTO.builder().response(message).build();
  }

  public GetAllAdminResponseDTO mapToGetAllAdminDTO(AdminUsers adminUsers) {
    return GetAllAdminResponseDTO.builder()
        .id(adminUsers.getId())
        .name(adminUsers.getName())
        .role(adminUsers.getRole().name())
        .email(adminUsers.getEmail())
        .phoneNumber(adminUsers.getPhoneNumber())
        .build();
  }

  public GetAdminResponseDTO mapToGetAdminDTO(AdminUsers adminUsers) {
    return GetAdminResponseDTO.builder()
        .name(adminUsers.getName())
        .phoneNumber(adminUsers.getPhoneNumber())
        .imageUrl(adminUsers.getImageUrl())
        .build();
  }
}
