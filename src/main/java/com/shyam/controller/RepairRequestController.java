package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.RepairRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Repair Request", description = "Repair request management endpoints")
public class RepairRequestController {
  private final RepairRequestService repairRequestService;

  @Operation(
      summary = "Get all repair requests",
      description = "Retrieve a list of all repair requests.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/getAllRepairRequests")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public BaseResponseDTO<GetAllRepairResponseDTO> getAllRepairRequests() {
    log.info("Entering getAllRepairRequests method");
    var response = repairRequestService.getAllRepairRequests();
    log.info("Exiting getAllRepairRequests method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Search repair requests",
      description = "Search repair requests based on criteria.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/searchRepairRequest")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public BaseResponseDTO<GetAllRepairResponseDTO> searchRepairRequest(
      @Valid @RequestBody SearchRepairRequestDTO searchRepairRequestDTO) {
    log.info("Entering searchRepairRequest method with dto: {}", searchRepairRequestDTO);
    var response = repairRequestService.searchRepairRequest(searchRepairRequestDTO);
    log.info("Exiting searchRepairRequest method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get repair request by ID",
      description = "Retrieve a specific repair request by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "Repair request not found",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/getRepairRequestById")
  public BaseResponseDTO<RepairRequestResponseDTO> getRepairRequestById(
      @Valid @RequestBody RepairRequestRequestDTO repairRequestRequestDTO) {
    log.info("Entering getRepairRequestById method with dto: {}", repairRequestRequestDTO);
    var response = repairRequestService.getRepairRequestById(repairRequestRequestDTO);
    log.info("Exiting getRepairRequestById method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Create repair request", description = "Create a new repair request.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Repair request created successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/createRepairRequest")
  public BaseResponseDTO<CreateRepairResponseDTO> createRepairRequest(
      @Valid @RequestBody CreateRepairRequestDTO createRepairRequestDTO) {
    log.info("Entering createRepairRequest method with dto: {}", createRepairRequestDTO);
    var response = repairRequestService.createRepairRequest(createRepairRequestDTO);
    log.info("Repair request created successfully");
    log.info("Exiting createRepairRequest method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Edit repair request", description = "Edit an existing repair request.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Repair request updated successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "Repair request not found",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PutMapping("/editRepairRequest")
  public BaseResponseDTO<EditRepairResponseDTO> editRepairRequest(
      @Valid @RequestBody EditRepairRequestDTO editRepairRequestDTO) {
    log.info("Entering editRepairRequest method with dto: {}", editRepairRequestDTO);
    var response = repairRequestService.editRepairRequest(editRepairRequestDTO);
    log.info("Repair request updated successfully");
    log.info("Exiting editRepairRequest method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Delete repair request", description = "Delete a repair request by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Repair request deleted successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "Repair request not found",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @DeleteMapping("/deleteRepairRequest")
  public BaseResponseDTO<DeleteRepairResponseDTO> deleteRepairRequest(
      @Valid @RequestBody DeleteRepairRequestDTO deleteRepairRequestDTO) {
    log.info("Entering deleteRepairRequest method with dto: {}", deleteRepairRequestDTO);
    var response = repairRequestService.deleteRepairRequest(deleteRepairRequestDTO);
    log.info("Repair request deleted successfully");
    log.info("Exiting deleteRepairRequest method");
    return new BaseResponseDTO<>(response, null);
  }
}
