package com.shyam.service.Imp;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurityServiceImpl implements PurityService {

    private final PurityRepository purityRepository;
    private final MaterialTypeRepository materialTypeRepository;
    private final MessageSourceUtil messageSourceUtil;
    private final PurityMapper purityMapper;

    @Override
    @Transactional
    public AddPurityResponseDTO addPurity(AddPurityRequestDTO requestDTO) {
        MaterialType materialType = materialTypeRepository.findById(requestDTO.getMaterialTypeId())
                .orElseThrow(() -> new RuntimeException("Material type not found"));

        if (purityRepository.existsByMaterialTypeAndPurityName(materialType, requestDTO.getPurityName())) {
            throw new RuntimeException("Purity with this name already exists for the given material type");
        }

        Purity purity = Purity.builder()
                .materialType(materialType)
                .purityName(requestDTO.getPurityName())
                .purityFactor(requestDTO.getPurityFactor())
                .createdBy(requestDTO.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .status(true)
                .build();

        purityRepository.save(purity);
        return purityMapper.mapToPurity(messageSourceUtil.getMessage("purity.added"));
    }

    @Override
    @Transactional
    public AddPurityResponseDTO updatePurity(UpdatePurityRequestDTO requestDTO) {
        Purity existing = purityRepository.findById(requestDTO.getPurityId())
                .orElseThrow(() -> new RuntimeException("Purity not found"));

        MaterialType materialType = materialTypeRepository.findById(requestDTO.getMaterialTypeId())
                .orElseThrow(() -> new RuntimeException("Material type not found"));

        if (!(existing.getMaterialType().getMaterialTypeId().equals(requestDTO.getMaterialTypeId())
                && existing.getPurityName().equalsIgnoreCase(requestDTO.getPurityName()))
                && purityRepository.existsByMaterialTypeAndPurityName(materialType, requestDTO.getPurityName())) {
            throw new RuntimeException("Purity with this name already exists for the given material type");
        }

        existing.setMaterialType(materialType);
        existing.setPurityName(requestDTO.getPurityName());
        existing.setPurityFactor(requestDTO.getPurityFactor());
        existing.setUpdatedBy(requestDTO.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());

        purityRepository.save(existing);
        return purityMapper.mapToPurity(messageSourceUtil.getMessage("purity.updated"));
    }

    @Override
    @Transactional
    public AddPurityResponseDTO deletePurity(GetPurityByIdRequestDTO requestDTO) {
        Purity existing = purityRepository.findById(requestDTO.getPurityId())
                .orElseThrow(() -> new RuntimeException("Purity not found"));
        purityRepository.delete(existing);
        return purityMapper.mapToPurity(messageSourceUtil.getMessage("purity.deleted"));
    }

    @Override
    public GetPurityResponseDTO getPurityById(GetPurityByIdRequestDTO requestDTO) {
        Purity purity = purityRepository.findById(requestDTO.getPurityId())
                .orElseThrow(() -> new RuntimeException("Purity not found"));
        return GetPurityResponseDTO.fromEntity(purity);
    }

    @Override
    public List<GetPurityResponseDTO> getAllPurities() {
        return purityRepository.findAll().stream()
                .map(GetPurityResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetPurityResponseDTO> getPuritiesByMaterialType(Long materialTypeId) {
        return purityRepository.findByMaterialType_MaterialTypeIdAndStatusTrue(materialTypeId).stream()
                .map(GetPurityResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetPurityResponseDTO> getAllActivePurities() {
        return purityRepository.findByStatusTrue().stream()
                .map(GetPurityResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}