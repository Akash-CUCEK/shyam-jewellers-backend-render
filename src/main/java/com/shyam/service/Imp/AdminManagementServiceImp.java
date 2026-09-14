package com.shyam.service.Imp;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.util.MapperUtil;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.constants.MessageConstant;
import com.shyam.dao.AdminDAO;
import com.shyam.dto.NotificationMessage;
import com.shyam.dto.NotificationType;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.entity.AdminUsers;
import com.shyam.mapper.AdminMapper;
import com.shyam.service.NotificationService;
import com.shyam.service.AdminManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminManagementServiceImp implements AdminManagementService {
    private final AdminMapper adminMapper;
    private final MessageSourceUtil messageSourceUtil;
    private final AdminDAO adminDAO;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public EditAdminResponseDTO edit(EditAdminRequestDTO editAdminRequestDTO) {
        log.info("Processing edit  ");
        var admin = adminDAO.findUserByEmail(editAdminRequestDTO.getEmail());
        admin.setName(editAdminRequestDTO.getName());
        admin.setPhoneNumber(editAdminRequestDTO.getPhoneNumber());
        admin.setImageUrl(editAdminRequestDTO.getImageUrl());
        adminDAO.save(admin);
        NotificationMessage message =
            new NotificationMessage(admin.getEmail(), null, null, NotificationType.UPDATE);
        notificationService.process(message);
        return adminMapper.mapToAdminEditInMessage(
            messageSourceUtil.getMessage(MESSAGE_CODE_EDIT_ADMIN));
    }

    @Override
    @Transactional
    public RegisterResponseDTO registerAdmin(RegisterRequestDTO registerRequestDTO) {
        if (adminDAO.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new SYMException(
                org.springframework.http.HttpStatus.CONFLICT,
                SYMErrorType.GENERIC_EXCEPTION,
                ErrorCodeConstants.ERROR_CODE_EMAIL_ALREADY_EXISTS,
                "Email already registered!",
                "Attempted to register with existing email: " + registerRequestDTO.getEmail());
        }

        var newUser = new AdminUsers();
        newUser.setName(registerRequestDTO.getName());
        newUser.setEmail(registerRequestDTO.getEmail());
        newUser.setPhoneNumber(registerRequestDTO.getPhoneNumber());
        newUser.setRole(MapperUtil.parseRole("ADMIN"));
        adminDAO.save(newUser);
        NotificationMessage message =
            new NotificationMessage(
                registerRequestDTO.getEmail(), null, null, NotificationType.WELCOME);
        notificationService.process(message);
        return adminMapper.mapToRegisterAdminInMessage(
            messageSourceUtil.getMessage(MESSAGE_CODE_REGISTER_ADMIN));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetOfferPhotoResponseDTO> getOfferPhoto() {
        // This method doesn't belong here - it belongs to OfferService
        // I'll leave it empty for now and fix it in the OfferServiceImp
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public GetAdminListResponseDTO getAllAdmin() {

        logger.info("Processing request to get all admins");

        var roles = List.of(com.shyam.common.constants.Role.ADMIN, com.shyam.common.constants.Role.SUPER_ADMIN);

        var admins = adminDAO.findByRoleIn(roles);

        var responseDTOList =
            admins.stream().map(adminMapper::mapToGetAllAdminDTO).collect(Collectors.toList());

        return GetAdminListResponseDTO.builder().getAllAdminResponseDTOList(responseDTOList).build();
    }

    @Override
    @Transactional
    public DeleteAdminResponseDTO deleteAdmin(DeleteAdminRequestDTO deleteAdmin) {
        var admin = adminDAO.findUserByEmail(deleteAdmin.getEmail());
        adminDAO.delete(admin);
        return adminMapper.mapToDeleteAdminInMessage(
            messageSourceUtil.getMessage(MESSAGE_CODE_DELETE_ADMIN));
    }

    @Override
    @Transactional(readOnly = true)
    public GetAdminResponseDTO getAdmin(GetAdminRequestDTO getAdminRequestDTO) {

        logger.info("Processing request to get admin");

        var admin = adminDAO.findByEmail(getAdminRequestDTO.getEmail());

        return adminMapper.mapToGetAdminDTO(admin.get());
    }
}