package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.RepairRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Slf4j
public class RepairRequestController {
  private final RepairRequestService repairRequestService;

  @PostMapping("/getAllRepairRequests")
  public BaseResponseDTO<GetAllRepairResponseDTO> getAllRepairRequests() {
    log.info("Received request for getting all repair requests");
    var response = repairRequestService.getAllRepairRequests();
    return new BaseResponseDTO<>(response, null);
  }

  @PostMapping("/searchRepairRequest")
  public BaseResponseDTO<GetAllRepairResponseDTO> searchRepairRequest(
      @RequestBody SearchRepairRequestDTO createRepairRequestDTO) {
    log.info("Received request for search repair request ");
    var response = repairRequestService.searchRepairRequest(createRepairRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @PostMapping("/getRepairRequestById")
  public BaseResponseDTO<RepairRequestResponseDTO> getAllRepairRequests(
      @RequestBody RepairRequestRequestDTO repairRequestRequestDTO) {

    log.info("Received request for getting repair request");
    var response = repairRequestService.getRepairRequestById(repairRequestRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @PostMapping("/createRepairRequest")
  public BaseResponseDTO<CreateRepairResponseDTO> createRepairRequest(
      @RequestBody CreateRepairRequestDTO createRepairRequestDTO) {
    log.info("Received request for create repair request");
    var response = repairRequestService.createRepairRequest(createRepairRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @PutMapping("/editRepairRequest")
  public BaseResponseDTO<EditRepairResponseDTO> createRepairRequest(
      @RequestBody EditRepairRequestDTO editRepairRequestDTO) {
    log.info("Received request for edit repair request");
    var response = repairRequestService.editRepairRequest(editRepairRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @DeleteMapping("/deleteRepairRequest")
  public BaseResponseDTO<DeleteRepairResponseDTO> deleteRepairRequest(
      @RequestBody DeleteRepairRequestDTO deleteRepairRequestDTO) {
    log.info("Received request for delete repair request");
    var response = repairRequestService.deleteRepairRequest(deleteRepairRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }
}
