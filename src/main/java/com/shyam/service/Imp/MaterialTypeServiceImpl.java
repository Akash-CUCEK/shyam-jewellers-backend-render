package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.*;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dto.request.AddMaterialTypeRequestDTO;
import com.shyam.dto.request.GetMaterialTypeByIdRequestDTO;
import com.shyam.dto.request.UpdateMaterialTypeRequestDTO;
import com.shyam.dto.response.AddMaterialTypeResponseDTO;
import com.shyam.dto.response.GetMaterialTypeResponseDTO;
import com.shyam.entity.MaterialType;
import com.shyam.mapper.MaterialTypeMapper;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.service.MaterialTypeService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialTypeServiceImpl implements MaterialTypeService {

  private final MaterialTypeRepository materialTypeRepository;
  private final MessageSourceUtil messageSourceUtil;
  private final MaterialTypeMapper materialTypeMapper;

  @Override
  public AddMaterialTypeResponseDTO addMaterialType(AddMaterialTypeRequestDTO requestDTO) {
    log.info("Processing request to add material type: {}", requestDTO.getName());
    if (materialTypeRepository.existsByName(requestDTO.getName())) {
      log.warn("Material type with name {} already exists", requestDTO.getName());
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "MATERIAL_TYPE_ALREADY_EXISTS",
          "Material type with this name already exists",
          "Material type with this name already exists");
    }

    MaterialType materialType =
        MaterialType.builder()
            .name(requestDTO.getName())
            .createdBy(requestDTO.getCreatedBy())
            .createdAt(LocalDateTime.now())
            .status(true)
            .build();

    MaterialType saved = materialTypeRepository.save(materialType);
    log.info("Material type added successfully with id: {}", saved.getMaterialTypeId());
    return materialTypeMapper.mapToMaterialType(
        messageSourceUtil.getMessage(MESSAGE_CODE_ADD_MATERIAL_TYPE));
  }

  @Override
  public AddMaterialTypeResponseDTO updateMaterialType(UpdateMaterialTypeRequestDTO requestDTO) {
    log.info("Processing request to update material type id: {}", requestDTO.getMaterialTypeId());
    MaterialType existing =
        materialTypeRepository
            .findById(requestDTO.getMaterialTypeId())
            .orElseThrow(
                () -> {
                  log.warn("Material type not found with id: {}", requestDTO.getMaterialTypeId());
                  return new SYMException(
                      HttpStatus.NOT_FOUND,
                      SYMErrorType.GENERIC_EXCEPTION,
                      "MATERIAL_TYPE_NOT_FOUND",
                      "Material type not found",
                      "Material type not found");
                });

    if (!existing.getName().equalsIgnoreCase(requestDTO.getName())
        && materialTypeRepository.existsByName(requestDTO.getName())) {
      log.warn("Material type with name {} already exists", requestDTO.getName());
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "MATERIAL_TYPE_ALREADY_EXISTS",
          "Material type with this name already exists",
          "Material type with this name already exists");
    }

    existing.setName(requestDTO.getName());
    existing.setUpdatedBy(requestDTO.getUpdatedBy());
    existing.setUpdatedAt(LocalDateTime.now());

    MaterialType updated = materialTypeRepository.save(existing);
    log.info("Material type updated successfully with id: {}", updated.getMaterialTypeId());
    return materialTypeMapper.mapToMaterialType(
        messageSourceUtil.getMessage(MESSAGE_CODE_UPDATE_MATERIAL_TYPE));
  }

  @Override
  public AddMaterialTypeResponseDTO deleteMaterialType(GetMaterialTypeByIdRequestDTO requestDTO) {
    log.info("Processing request to delete material type id: {}", requestDTO.getMaterialTypeId());
    MaterialType existing =
        materialTypeRepository
            .findById(requestDTO.getMaterialTypeId())
            .orElseThrow(
                () -> {
                  log.warn("Material type not found with id: {}", requestDTO.getMaterialTypeId());
                  return new SYMException(
                      HttpStatus.NOT_FOUND,
                      SYMErrorType.GENERIC_EXCEPTION,
                      "MATERIAL_TYPE_NOT_FOUND",
                      "Material type not found",
                      "Material type not found");
                });
    materialTypeRepository.delete(existing);
    log.info("Material type deleted successfully with id: {}", requestDTO.getMaterialTypeId());
    return materialTypeMapper.mapToMaterialType(
        messageSourceUtil.getMessage(MESSAGE_CODE_DELETE_MATERIAL_TYPE));
  }

  @Override
  public GetMaterialTypeResponseDTO getMaterialTypeById(GetMaterialTypeByIdRequestDTO requestDTO) {
    log.info("Processing request to get material type by id: {}", requestDTO.getMaterialTypeId());
    MaterialType materialType =
        materialTypeRepository
            .findById(requestDTO.getMaterialTypeId())
            .orElseThrow(
                () -> {
                  log.warn("Material type not found with id: {}", requestDTO.getMaterialTypeId());
                  return new SYMException(
                      HttpStatus.NOT_FOUND,
                      SYMErrorType.GENERIC_EXCEPTION,
                      "MATERIAL_TYPE_NOT_FOUND",
                      "Material type not found",
                      "Material type not found");
                });
    log.info("Material type retrieved successfully with id: {}", requestDTO.getMaterialTypeId());
    return GetMaterialTypeResponseDTO.fromEntity(materialType);
  }

  @Override
  public List<GetMaterialTypeResponseDTO> getAllMaterialTypes() {
    log.info("Processing request to get all material types");
    List<MaterialType> materialTypes = materialTypeRepository.findAll();
    List<GetMaterialTypeResponseDTO> result =
        materialTypes.stream()
            .map(GetMaterialTypeResponseDTO::fromEntity)
            .collect(Collectors.toList());
    log.info("Retrieved {} material types", result.size());
    return result;
  }
}
