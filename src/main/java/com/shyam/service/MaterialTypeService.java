package com.shyam.service;

import com.shyam.dto.request.AddMaterialTypeRequestDTO;
import com.shyam.dto.request.GetMaterialTypeByIdRequestDTO;
import com.shyam.dto.request.UpdateMaterialTypeRequestDTO;
import com.shyam.dto.response.AddMaterialTypeResponseDTO;
import com.shyam.dto.response.GetMaterialTypeResponseDTO;
import java.util.List;

public interface MaterialTypeService {

  AddMaterialTypeResponseDTO addMaterialType(AddMaterialTypeRequestDTO requestDTO);

  AddMaterialTypeResponseDTO updateMaterialType(UpdateMaterialTypeRequestDTO requestDTO);

  AddMaterialTypeResponseDTO deleteMaterialType(GetMaterialTypeByIdRequestDTO requestDTO);

  GetMaterialTypeResponseDTO getMaterialTypeById(GetMaterialTypeByIdRequestDTO requestDTO);

  List<GetMaterialTypeResponseDTO> getAllMaterialTypes();
}
