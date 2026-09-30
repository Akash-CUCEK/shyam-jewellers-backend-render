package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.AddPurityRequestDTO;
import com.shyam.dto.request.GetPurityByIdRequestDTO;
import com.shyam.dto.request.UpdatePurityRequestDTO;
import com.shyam.dto.response.AddPurityResponseDTO;
import com.shyam.dto.response.GetPurityResponseDTO;
import com.shyam.service.PurityService;
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
@Tag(name = "Admin Purity", description = "Admin purity management endpoints")
public class AdminPurityController {

  private final PurityService purityService;

  @Operation(summary = "Add purity", description = "Add a new purity for a material type.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Purity added successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/addPurity")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<AddPurityResponseDTO> addPurity(
      @Valid @RequestBody AddPurityRequestDTO requestDTO) {
    log.info("Entering addPurity method with dto: {}", requestDTO);
    var response = purityService.addPurity(requestDTO);
    log.info("Purity added successfully");
    log.info("Exiting addPurity method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Update purity", description = "Update an existing purity.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Purity updated successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PutMapping("/updatePurity")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<AddPurityResponseDTO> updatePurity(
      @Valid @RequestBody UpdatePurityRequestDTO requestDTO) {
    log.info("Entering updatePurity method with dto: {}", requestDTO);
    var response = purityService.updatePurity(requestDTO);
    log.info("Purity updated successfully");
    log.info("Exiting updatePurity method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Delete purity", description = "Delete a purity by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Purity deleted successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Purity not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @DeleteMapping("/deletePurity")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<AddPurityResponseDTO> deletePurity(
      @Valid @RequestBody GetPurityByIdRequestDTO requestDTO) {
    log.info("Entering deletePurity method with dto: {}", requestDTO);
    var response = purityService.deletePurity(requestDTO);
    log.info("Purity deleted successfully");
    log.info("Exiting deletePurity method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Get all purities", description = "Get list of all purities.")
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
  @PostMapping("/getAllPurities")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<List<GetPurityResponseDTO>> getAllPurities() {
    log.info("Entering getAllPurities method");
    var response = purityService.getAllPurities();
    log.info("Exiting getAllPurities method");
    return new BaseResponseDTO<>(response, null);
  }
}
