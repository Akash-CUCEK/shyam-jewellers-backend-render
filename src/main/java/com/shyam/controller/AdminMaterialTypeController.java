package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.AddMaterialTypeRequestDTO;
import com.shyam.dto.request.GetMaterialTypeByIdRequestDTO;
import com.shyam.dto.request.UpdateMaterialTypeRequestDTO;
import com.shyam.dto.response.AddMaterialTypeResponseDTO;
import com.shyam.dto.response.GetMaterialTypeResponseDTO;
import com.shyam.service.MaterialTypeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminMaterialTypeController {

    private final MaterialTypeService materialTypeService;

    @Operation(summary = "Add material type", description = "Add a new material type.")
    @PostMapping("/addMaterialType")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public BaseResponseDTO<AddMaterialTypeResponseDTO> addMaterialType(
            @Valid @RequestBody AddMaterialTypeRequestDTO requestDTO) {
        log.info("Received request to add material type: {}", requestDTO.getName());
        var response = materialTypeService.addMaterialType(requestDTO);
        return new BaseResponseDTO<>(response, null);
    }

    @Operation(summary = "Update material type", description = "Update an existing material type.")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/updateMaterialType")
    public BaseResponseDTO<AddMaterialTypeResponseDTO> updateMaterialType(
            @Valid @RequestBody UpdateMaterialTypeRequestDTO requestDTO) {
        log.info("Received request to update material type id: {}", requestDTO.getMaterialTypeId());
        var response = materialTypeService.updateMaterialType(requestDTO);
        return new BaseResponseDTO<>(response, null);
    }

    @Operation(summary = "Delete material type", description = "Delete a material type by its ID.")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/deleteMaterialType")
    public BaseResponseDTO<AddMaterialTypeResponseDTO> deleteMaterialType(
            @Valid @RequestBody GetMaterialTypeByIdRequestDTO requestDTO) {
        log.info("Received request to delete material type id: {}", requestDTO.getMaterialTypeId());
        var response = materialTypeService.deleteMaterialType(requestDTO);
        return new BaseResponseDTO<>(response, null);
    }

    @Operation(summary = "Get all material types", description = "Get list of all material types.")
    @PostMapping("/getAllMaterialTypes")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public BaseResponseDTO<List<GetMaterialTypeResponseDTO>> getAllMaterialTypes() {
        log.info("Received request to get all material types");
        var response = materialTypeService.getAllMaterialTypes();
        return new BaseResponseDTO<>(response, null);
    }

}
