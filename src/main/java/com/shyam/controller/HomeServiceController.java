package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
  @PostMapping("/getAllServiceRequests")
  public BaseResponseDTO<GetAllHomeServiceResponseDTO> getAllHomeServiceRequests() {
    log.info("Entering getAllHomeServiceRequests method");
    var response = homeService.getAllHomeServiceRequests();
    log.info("Exiting getAllHomeServiceRequests method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get home service request by ID",
      description = "Retrieve a specific home service request by its ID.")
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
        description = "Home service request not found",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/getHomeServiceRequestById")
  public BaseResponseDTO<HomeServiceResponseDTO> getHomeServiceRequestById(
      @Valid @RequestBody HomeServiceRequestDTO homeServiceRequestDTO) {
    log.info("Entering getHomeServiceRequestById method with dto: {}", homeServiceRequestDTO);
    var response = homeService.getHomeServiceRequestById(homeServiceRequestDTO);
    log.info("Exiting getHomeServiceRequestById method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Create home service request",
      description = "Create a new home service request.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Home service request created successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/createHomeServiceRequest")
  public BaseResponseDTO<CreateHomeServiceResponseDTO> createHomeServiceRequests(
      @Valid @RequestBody CreateHomeServiceRequestDTO createHomeServiceRequestDTO) {
    log.info("Entering createHomeServiceRequests method with dto: {}", createHomeServiceRequestDTO);
    var response = homeService.createHomeServiceRequests(createHomeServiceRequestDTO);
    log.info("Home service request created successfully");
    log.info("Exiting createHomeServiceRequests method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Edit home service request",
      description = "Edit an existing home service request.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Home service request updated successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "Home service request not found",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PutMapping("/editHomeServiceRequest")
  public BaseResponseDTO<EditHomeServiceResponseDTO> editHomeServiceRequest(
      @Valid @RequestBody EditHomeServiceRequestDTO editHomeServiceRequestDTO) {
    log.info("Entering editHomeServiceRequest method with dto: {}", editHomeServiceRequestDTO);
    var response = homeService.editHomeServiceRequest(editHomeServiceRequestDTO);
    log.info("Home service request updated successfully");
    log.info("Exiting editHomeServiceRequest method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Search home service requests",
      description = "Search home service requests based on criteria.")
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
  @PostMapping("/searchHomeServiceRequest")
  public BaseResponseDTO<GetAllHomeServiceResponseDTO> searchHomeServiceRequest(
      @Valid @RequestBody SearchHomeServiceRequestDTO searchHomeServiceRequestDTO) {
    log.info("Entering searchHomeServiceRequest method with dto: {}", searchHomeServiceRequestDTO);
    var response = homeService.searchHomeServiceRequest(searchHomeServiceRequestDTO);
    log.info("Exiting searchHomeServiceRequest method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Delete home service request",
      description = "Delete a home service request by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Home service request deleted successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "Home service request not found",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @DeleteMapping("/deleteHomeServiceRequest")
  public BaseResponseDTO<DeleteHomeServiceResponseDTO> deleteHomeServiceRequest(
      @Valid @RequestBody DeleteHomeServiceRequestDTO deleteHomeServiceRequestDTO) {
    log.info("Entering deleteHomeServiceRequest method with dto: {}", deleteHomeServiceRequestDTO);
    var response = homeService.deleteHomeServiceRequest(deleteHomeServiceRequestDTO);
    log.info("Home service request deleted successfully");
    log.info("Exiting deleteHomeServiceRequest method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get all user service requests",
      description = "Retrieve a list of all service requests for the current user.")
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
  @PostMapping("/getAllUserServiceRequests")
  public BaseResponseDTO<GetAllHomeServiceResponseDTO> getAllUserServiceRequests() {
    log.info("Entering getAllUserServiceRequests method");
    var response = homeService.getAllUserServiceRequests();
    log.info("Exiting getAllUserServiceRequests method");
    return new BaseResponseDTO<>(response, null);
  }
}
