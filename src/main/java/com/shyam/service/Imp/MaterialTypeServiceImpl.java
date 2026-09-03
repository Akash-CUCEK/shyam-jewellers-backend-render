package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.*;

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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaterialTypeServiceImpl implements MaterialTypeService {

  private final MaterialTypeRepository materialTypeRepository;
  private final MessageSourceUtil messageSourceUtil;
  private final MaterialTypeMapper materialTypeMapper;

  @Override
  public AddMaterialTypeResponseDTO addMaterialType(AddMaterialTypeRequestDTO requestDTO) {
    if (materialTypeRepository.existsByName(requestDTO.getName())) {
      throw new RuntimeException("Material type with this name already exists");
    }

    MaterialType materialType =
        MaterialType.builder()
            .name(requestDTO.getName())
            .createdBy(requestDTO.getCreatedBy())
            .createdAt(LocalDateTime.now())
            .status(true)
            .build();

    MaterialType saved = materialTypeRepository.save(materialType);
    return materialTypeMapper.mapToMaterialType(
        messageSourceUtil.getMessage(MESSAGE_CODE_ADD_MATERIAL_TYPE));
  }

  @Override
  public AddMaterialTypeResponseDTO updateMaterialType(UpdateMaterialTypeRequestDTO requestDTO) {
    MaterialType existing =
        materialTypeRepository
            .findById(requestDTO.getMaterialTypeId())
            .orElseThrow(() -> new RuntimeException("Material type not found"));

    if (!existing.getName().equalsIgnoreCase(requestDTO.getName())
        && materialTypeRepository.existsByName(requestDTO.getName())) {
      throw new RuntimeException("Material type with this name already exists");
    }

    existing.setName(requestDTO.getName());
    existing.setUpdatedBy(requestDTO.getUpdatedBy());
    existing.setUpdatedAt(LocalDateTime.now());

    MaterialType updated = materialTypeRepository.save(existing);
    return materialTypeMapper.mapToMaterialType(
        messageSourceUtil.getMessage(MESSAGE_CODE_UPDATE_MATERIAL_TYPE));
  }

  @Override
  public AddMaterialTypeResponseDTO deleteMaterialType(GetMaterialTypeByIdRequestDTO requestDTO) {
    MaterialType existing =
        materialTypeRepository
            .findById(requestDTO.getMaterialTypeId())
            .orElseThrow(() -> new RuntimeException("Material type not found"));
    materialTypeRepository.delete(existing);
    return materialTypeMapper.mapToMaterialType(
        messageSourceUtil.getMessage(MESSAGE_CODE_DELETE_MATERIAL_TYPE));
  }

  @Override
  public GetMaterialTypeResponseDTO getMaterialTypeById(GetMaterialTypeByIdRequestDTO requestDTO) {
    MaterialType materialType =
        materialTypeRepository
            .findById(requestDTO.getMaterialTypeId())
            .orElseThrow(() -> new RuntimeException("Material type not found"));
    return GetMaterialTypeResponseDTO.fromEntity(materialType);
  }

  @Override
  public List<GetMaterialTypeResponseDTO> getAllMaterialTypes() {
    List<MaterialType> materialTypes = materialTypeRepository.findAll();
    return materialTypes.stream()
        .map(GetMaterialTypeResponseDTO::fromEntity)
        .collect(Collectors.toList());
  }
}
