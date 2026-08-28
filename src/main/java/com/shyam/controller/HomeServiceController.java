package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/homeService")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Home Service", description = "Home service request management endpoints")
public class HomeServiceController {
  private final HomeService homeService;

  @Operation(
      summary = "Get all home service requests",
      description = "Retrieve a list of all home service requests.")
  @PostMapping("/getAllServiceRequests")
  public BaseResponseDTO<GetAllHomeServiceResponseDTO> getAllHomeServiceRequests() {
    log.info("Received request for getting all home service request");
    var response = homeService.getAllHomeServiceRequests();
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get home service request by ID",
      description = "Retrieve a specific home service request by its ID.")
  @PostMapping("/getHomeServiceRequestById")
  public BaseResponseDTO<HomeServiceResponseDTO> getHomeServiceRequestById(
      @RequestBody HomeServiceRequestDTO homeServiceRequestDTO) {
    log.info("Received request for getting home service request");
    var response = homeService.getHomeServiceRequestById(homeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Create home service request",
      description = "Create a new home service request.")
  @PostMapping("/createHomeServiceRequest")
  public BaseResponseDTO<CreateHomeServiceResponseDTO> createHomeServiceRequests(
      @RequestBody CreateHomeServiceRequestDTO createHomeServiceRequestDTO) {
    log.info("Received request for create home service request");
    var response = homeService.createHomeServiceRequests(createHomeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Edit home service request",
      description = "Edit an existing home service request.")
  @PutMapping("/editHomeServiceRequest")
  public BaseResponseDTO<EditHomeServiceResponseDTO> editHomeServiceRequest(
      @RequestBody EditHomeServiceRequestDTO editHomeServiceRequestDTO) {
    log.info("Received request for edit home service request");
    var response = homeService.editHomeServiceRequest(editHomeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Search home service requests",
      description = "Search home service requests based on criteria.")
  @PostMapping("/searchHomeServiceRequest")
  public BaseResponseDTO<GetAllHomeServiceResponseDTO> searchHomeServiceRequest(
      @RequestBody SearchHomeServiceRequestDTO searchHomeServiceRequestDTO) {
    log.info("Received request for search home service request");
    var response = homeService.searchHomeServiceRequest(searchHomeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Delete home service request",
      description = "Delete a home service request by its ID.")
  @DeleteMapping("/deleteHomeServiceRequest")
  public BaseResponseDTO<DeleteHomeServiceResponseDTO> deleteHomeServiceRequest(
      @RequestBody DeleteHomeServiceRequestDTO editHomeServiceRequestDTO) {
    log.info("Received request for delete home service request");
    var response = homeService.deleteHomeServiceRequest(editHomeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get all user service requests",
      description = "Retrieve a list of all service requests for the current user.")
  @PostMapping("/getAllUserServiceRequests")
  public BaseResponseDTO<GetAllHomeServiceResponseDTO> getAllUserServiceRequests() {
    log.info("Received request for getting all home service request for user");
    var response = homeService.getAllUserServiceRequests();
    return new BaseResponseDTO<>(response, null);
  }
}
