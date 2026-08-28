package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.RepairRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  @PostMapping("/getAllRepairRequests")
  public BaseResponseDTO<GetAllRepairResponseDTO> getAllRepairRequests() {
    log.info("Received request for getting all repair requests");
    var response = repairRequestService.getAllRepairRequests();
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Search repair requests",
      description = "Search repair requests based on criteria.")
  @PostMapping("/searchRepairRequest")
  public BaseResponseDTO<GetAllRepairResponseDTO> searchRepairRequest(
      @RequestBody SearchRepairRequestDTO createRepairRequestDTO) {
    log.info("Received request for search repair request ");
    var response = repairRequestService.searchRepairRequest(createRepairRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get repair request by ID",
      description = "Retrieve a specific repair request by its ID.")
  @PostMapping("/getRepairRequestById")
  public BaseResponseDTO<RepairRequestResponseDTO> getAllRepairRequests(
      @RequestBody RepairRequestRequestDTO repairRequestRequestDTO) {

    log.info("Received request for getting repair request");
    var response = repairRequestService.getRepairRequestById(repairRequestRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Create repair request", description = "Create a new repair request.")
  @PostMapping("/createRepairRequest")
  public BaseResponseDTO<CreateRepairResponseDTO> createRepairRequest(
      @RequestBody CreateRepairRequestDTO createRepairRequestDTO) {
    log.info("Received request for create repair request");
    var response = repairRequestService.createRepairRequest(createRepairRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Edit repair request", description = "Edit an existing repair request.")
  @PutMapping("/editRepairRequest")
  public BaseResponseDTO<EditRepairResponseDTO> createRepairRequest(
      @RequestBody EditRepairRequestDTO editRepairRequestDTO) {
    log.info("Received request for edit repair request");
    var response = repairRequestService.editRepairRequest(editRepairRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Delete repair request", description = "Delete a repair request by its ID.")
  @DeleteMapping("/deleteRepairRequest")
  public BaseResponseDTO<DeleteRepairResponseDTO> deleteRepairRequest(
      @RequestBody DeleteRepairRequestDTO deleteRepairRequestDTO) {
    log.info("Received request for delete repair request");
    var response = repairRequestService.deleteRepairRequest(deleteRepairRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }
}
