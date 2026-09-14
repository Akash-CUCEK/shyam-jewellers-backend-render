package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.*;

import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dto.request.AddPurityRequestDTO;
import com.shyam.dto.request.GetPurityByIdRequestDTO;
import com.shyam.dto.request.UpdatePurityRequestDTO;
import com.shyam.dto.response.AddPurityResponseDTO;
import com.shyam.dto.response.GetPurityResponseDTO;
import com.shyam.entity.Purity;
import com.shyam.mapper.PurityMapper;
import com.shyam.repository.PurityRepository;
import com.shyam.service.PurityService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurityServiceImpl implements PurityService {

  private final PurityRepository purityRepository;
  private final MessageSourceUtil messageSourceUtil;
  private final PurityMapper purityMapper;

  @Override
  public AddPurityResponseDTO addPurity(AddPurityRequestDTO requestDTO) {
    if (purityRepository.existsByMaterialTypeAndPurityName(
        requestDTO.getMaterialType(), requestDTO.getPurityName())) {
      throw new RuntimeException("Purity with this name already exists for the given material type");
    }

    Purity purity =
        Purity.builder()
            .materialType(requestDTO.getMaterialType())
            .purityName(requestDTO.getPurityName())
            .purityFactor(requestDTO.getPurityFactor())
            .createdBy(requestDTO.getCreatedBy())
            .createdAt(LocalDateTime.now())
            .status(true)
            .build();

    Purity saved = purityRepository.save(purity);
    return purityMapper.mapToPurity(
        messageSourceUtil.getMessage(MESSAGE_CODE_ADD_PURITY));
  }

  @Override
  public AddPurityResponseDTO updatePurity(UpdatePurityRequestDTO requestDTO) {
    Purity existing =
        purityRepository
            .findById(requestDTO.getPurityId())
            .orElseThrow(() -> new RuntimeException("Purity not found"));

    if (!existing.getPurityName().equalsIgnoreCase(requestDTO.getPurityName())
        && purityRepository.existsByMaterialTypeAndPurityName(
            existing.getMaterialType(), requestDTO.getPurityName())) {
      throw new RuntimeException("Purity with this name already exists for the given material type");
    }

    existing.setPurityName(requestDTO.getPurityName());
    existing.setPurityFactor(requestDTO.getPurityFactor());
    existing.setUpdatedBy(requestDTO.getUpdatedBy());
    existing.setUpdatedAt(LocalDateTime.now());

    Purity updated = purityRepository.save(existing);
    return purityMapper.mapToPurity(
        messageSourceUtil.getMessage(MESSAGE_CODE_UPDATE_PURITY));
  }

  @Override
  public AddPurityResponseDTO deletePurity(GetPurityByIdRequestDTO requestDTO) {
    Purity existing =
        purityRepository
            .findById(requestDTO.getPurityId())
            .orElseThrow(() -> new RuntimeException("Purity not found"));
    purityRepository.delete(existing);
    return purityMapper.mapToPurity(
        messageSourceUtil.getMessage(MESSAGE_CODE_DELETE_PURITY));
  }

  @Override
  public GetPurityResponseDTO getPurityById(GetPurityByIdRequestDTO requestDTO) {
    Purity purity =
        purityRepository
            .findById(requestDTO.getPurityId())
            .orElseThrow(() -> new RuntimeException("Purity not found"));
    return GetPurityResponseDTO.fromEntity(purity);
  }

  @Override
  public List<GetPurityResponseDTO> getAllPurities() {
    List<Purity> purities = purityRepository.findAll();
    return purities.stream()
        .map(GetPurityResponseDTO::fromEntity)
        .collect(Collectors.toList());
  }
}