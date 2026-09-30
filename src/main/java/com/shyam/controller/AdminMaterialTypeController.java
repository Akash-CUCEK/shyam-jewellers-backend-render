package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.AddMaterialTypeRequestDTO;
import com.shyam.dto.request.GetMaterialTypeByIdRequestDTO;
import com.shyam.dto.request.UpdateMaterialTypeRequestDTO;
import com.shyam.dto.response.AddMaterialTypeResponseDTO;
import com.shyam.dto.response.GetMaterialTypeResponseDTO;
import com.shyam.service.MaterialTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/api/v1/admin")
@Tag(name = "Admin Material Type", description = "Admin material type management endpoints")
public class AdminMaterialTypeController {

  private final MaterialTypeService materialTypeService;

  @Operation(summary = "Add material type", description = "Add a new material type.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Material type added successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/addMaterialType")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<AddMaterialTypeResponseDTO> addMaterialType(
      @Valid @RequestBody AddMaterialTypeRequestDTO requestDTO) {
    log.info("Entering addMaterialType method with dto: {}", requestDTO);
    log.info("Received request to add material type: {}", requestDTO.getName());
    var response = materialTypeService.addMaterialType(requestDTO);
    log.info("Material type added successfully");
    log.info("Exiting addMaterialType method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Update material type", description = "Update an existing material type.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Material type updated successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PutMapping("/updateMaterialType")
  public BaseResponseDTO<AddMaterialTypeResponseDTO> updateMaterialType(
      @Valid @RequestBody UpdateMaterialTypeRequestDTO requestDTO) {
    log.info("Entering updateMaterialType method with dto: {}", requestDTO);
    log.info("Received request to update material type id: {}", requestDTO.getMaterialTypeId());
    var response = materialTypeService.updateMaterialType(requestDTO);
    log.info("Material type updated successfully");
    log.info("Exiting updateMaterialType method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Delete material type", description = "Delete a material type by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Material type deleted successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Material type not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @DeleteMapping("/deleteMaterialType")
  public BaseResponseDTO<AddMaterialTypeResponseDTO> deleteMaterialType(
      @Valid @RequestBody GetMaterialTypeByIdRequestDTO requestDTO) {
    log.info("Entering deleteMaterialType method with dto: {}", requestDTO);
    log.info("Received request to delete material type id: {}", requestDTO.getMaterialTypeId());
    var response = materialTypeService.deleteMaterialType(requestDTO);
    log.info("Material type deleted successfully");
    log.info("Exiting deleteMaterialType method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Get all material types", description = "Get list of all material types.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/getAllMaterialTypes")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<List<GetMaterialTypeResponseDTO>> getAllMaterialTypes() {
    log.info("Entering getAllMaterialTypes method");
    log.info("Received request to get all material types");
    var response = materialTypeService.getAllMaterialTypes();
    log.info("Exiting getAllMaterialTypes method");
    return new BaseResponseDTO<>(response, null);
  }
}
