package com.shyam.service;

import com.shyam.dto.request.AddPurityRequestDTO;
import com.shyam.dto.request.GetPurityByIdRequestDTO;
import com.shyam.dto.request.UpdatePurityRequestDTO;
import com.shyam.dto.response.AddPurityResponseDTO;
import com.shyam.dto.response.GetPurityResponseDTO;
import java.util.List;

public interface PurityService {
  AddPurityResponseDTO addPurity(AddPurityRequestDTO requestDTO);

  AddPurityResponseDTO updatePurity(UpdatePurityRequestDTO requestDTO);

  AddPurityResponseDTO deletePurity(GetPurityByIdRequestDTO requestDTO);

  GetPurityResponseDTO getPurityById(GetPurityByIdRequestDTO requestDTO);

  List<GetPurityResponseDTO> getAllPurities();

  List<GetPurityResponseDTO> getPuritiesByMaterialType(Long materialTypeId);

  List<GetPurityResponseDTO> getAllActivePurities();
}
