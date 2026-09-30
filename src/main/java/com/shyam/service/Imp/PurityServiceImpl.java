package com.shyam.service.Imp;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dto.request.AddPurityRequestDTO;
import com.shyam.dto.request.GetPurityByIdRequestDTO;
import com.shyam.dto.request.UpdatePurityRequestDTO;
import com.shyam.dto.response.AddPurityResponseDTO;
import com.shyam.dto.response.GetPurityResponseDTO;
import com.shyam.entity.MaterialType;
import com.shyam.entity.Purity;
import com.shyam.mapper.PurityMapper;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.PurityRepository;
import com.shyam.service.PurityService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurityServiceImpl implements PurityService {

  private final PurityRepository purityRepository;
  private final MaterialTypeRepository materialTypeRepository;
  private final MessageSourceUtil messageSourceUtil;
  private final PurityMapper purityMapper;

  @Override
  @Transactional
  public AddPurityResponseDTO addPurity(AddPurityRequestDTO requestDTO) {
    log.info(
        "Processing request to add purity for material type id: {}",
        requestDTO.getMaterialTypeId());
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

    if (purityRepository.existsByMaterialTypeAndPurityName(
        materialType, requestDTO.getPurityName())) {
      log.warn(
          "Purity with name {} already exists for material type id: {}",
          requestDTO.getPurityName(),
          requestDTO.getMaterialTypeId());
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "PURITY_ALREADY_EXISTS",
          "Purity with this name already exists for the given material type",
          "Purity with this name already exists for the given material type");
    }

    Purity purity =
        Purity.builder()
            .materialType(materialType)
            .purityName(requestDTO.getPurityName())
            .purityFactor(requestDTO.getPurityFactor())
            .createdBy(requestDTO.getCreatedBy())
            .createdAt(LocalDateTime.now())
            .status(true)
            .build();

    purityRepository.save(purity);
    log.info("Purity added successfully with id: {}", purity.getPurityId());
    return purityMapper.mapToPurity(messageSourceUtil.getMessage("purity.added"));
  }

  @Override
  @Transactional
  public AddPurityResponseDTO updatePurity(UpdatePurityRequestDTO requestDTO) {
    log.info("Processing request to update purity with id: {}", requestDTO.getPurityId());
    Purity existing =
        purityRepository
            .findById(requestDTO.getPurityId())
            .orElseThrow(
                () -> {
                  log.warn("Purity not found with id: {}", requestDTO.getPurityId());
                  return new SYMException(
                      HttpStatus.NOT_FOUND,
                      SYMErrorType.GENERIC_EXCEPTION,
                      "PURITY_NOT_FOUND",
                      "Purity not found",
                      "Purity not found");
                });

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

    if (!(existing.getMaterialType().getMaterialTypeId().equals(requestDTO.getMaterialTypeId())
            && existing.getPurityName().equalsIgnoreCase(requestDTO.getPurityName()))
        && purityRepository.existsByMaterialTypeAndPurityName(
            materialType, requestDTO.getPurityName())) {
      log.warn(
          "Purity with name {} already exists for material type id: {}",
          requestDTO.getPurityName(),
          requestDTO.getMaterialTypeId());
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          "PURITY_ALREADY_EXISTS",
          "Purity with this name already exists for the given material type",
          "Purity with this name already exists for the given material type");
    }

    existing.setMaterialType(materialType);
    existing.setPurityName(requestDTO.getPurityName());
    existing.setPurityFactor(requestDTO.getPurityFactor());
    existing.setUpdatedBy(requestDTO.getUpdatedBy());
    existing.setUpdatedAt(LocalDateTime.now());

    purityRepository.save(existing);
    log.info("Purity updated successfully with id: {}", existing.getPurityId());
    return purityMapper.mapToPurity(messageSourceUtil.getMessage("purity.updated"));
  }

  @Override
  @Transactional
  public AddPurityResponseDTO deletePurity(GetPurityByIdRequestDTO requestDTO) {
    log.info("Processing request to delete purity with id: {}", requestDTO.getPurityId());
    Purity existing =
        purityRepository
            .findById(requestDTO.getPurityId())
            .orElseThrow(
                () -> {
                  log.warn("Purity not found with id: {}", requestDTO.getPurityId());
                  return new SYMException(
                      HttpStatus.NOT_FOUND,
                      SYMErrorType.GENERIC_EXCEPTION,
                      "PURITY_NOT_FOUND",
                      "Purity not found",
                      "Purity not found");
                });
    purityRepository.delete(existing);
    log.info("Purity deleted successfully with id: {}", requestDTO.getPurityId());
    return purityMapper.mapToPurity(messageSourceUtil.getMessage("purity.deleted"));
  }

  @Override
  public GetPurityResponseDTO getPurityById(GetPurityByIdRequestDTO requestDTO) {
    log.info("Processing request to get purity by id: {}", requestDTO.getPurityId());
    Purity purity =
        purityRepository
            .findById(requestDTO.getPurityId())
            .orElseThrow(
                () -> {
                  log.warn("Purity not found with id: {}", requestDTO.getPurityId());
                  return new SYMException(
                      HttpStatus.NOT_FOUND,
                      SYMErrorType.GENERIC_EXCEPTION,
                      "PURITY_NOT_FOUND",
                      "Purity not found",
                      "Purity not found");
                });
    log.info("Purity retrieved successfully with id: {}", requestDTO.getPurityId());
    return GetPurityResponseDTO.fromEntity(purity);
  }

  @Override
  public List<GetPurityResponseDTO> getAllPurities() {
    log.info("Processing request to get all purities");
    List<GetPurityResponseDTO> result =
        purityRepository.findAll().stream()
            .map(GetPurityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    log.info("Retrieved {} purities", result.size());
    return result;
  }

  @Override
  public List<GetPurityResponseDTO> getPuritiesByMaterialType(Long materialTypeId) {
    log.info("Processing request to get purities by material type id: {}", materialTypeId);
    List<GetPurityResponseDTO> result =
        purityRepository.findByMaterialType_MaterialTypeIdAndStatusTrue(materialTypeId).stream()
            .map(GetPurityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    log.info("Retrieved {} purities for material type id: {}", result.size(), materialTypeId);
    return result;
  }

  @Override
  public List<GetPurityResponseDTO> getAllActivePurities() {
    log.info("Processing request to get all active purities");
    List<GetPurityResponseDTO> result =
        purityRepository.findByStatusTrue().stream()
            .map(GetPurityResponseDTO::fromEntity)
            .collect(Collectors.toList());
    log.info("Retrieved {} active purities", result.size());
    return result;
  }
}
