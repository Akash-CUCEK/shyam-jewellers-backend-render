package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/homeService")
@RequiredArgsConstructor
@Slf4j
public class HomeServiceController {
  private final HomeService homeService;

  @PostMapping("/getAllServiceRequests")
  public BaseResponseDTO<GetAllHomeServiceResponseDTO> getAllHomeServiceRequests() {
    log.info("Received request for getting all home service request");
    var response = homeService.getAllHomeServiceRequests();
    return new BaseResponseDTO<>(response, null);
  }

  @PostMapping("/getHomeServiceRequestById")
  public BaseResponseDTO<HomeServiceResponseDTO> getHomeServiceRequestById(
      @RequestBody HomeServiceRequestDTO homeServiceRequestDTO) {
    log.info("Received request for getting home service request");
    var response = homeService.getHomeServiceRequestById(homeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @PostMapping("/createHomeServiceRequest")
  public BaseResponseDTO<CreateHomeServiceResponseDTO> createHomeServiceRequests(
      @RequestBody CreateHomeServiceRequestDTO createHomeServiceRequestDTO) {
    log.info("Received request for create home service request");
    var response = homeService.createHomeServiceRequests(createHomeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @PutMapping("/editHomeServiceRequest")
  public BaseResponseDTO<EditHomeServiceResponseDTO> editHomeServiceRequest(
      @RequestBody EditHomeServiceRequestDTO editHomeServiceRequestDTO) {
    log.info("Received request for edit home service request");
    var response = homeService.editHomeServiceRequest(editHomeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @PostMapping("/searchHomeServiceRequest")
  public BaseResponseDTO<GetAllHomeServiceResponseDTO> searchHomeServiceRequest(
      @RequestBody SearchHomeServiceRequestDTO searchHomeServiceRequestDTO) {
    log.info("Received request for search home service request");
    var response = homeService.searchHomeServiceRequest(searchHomeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @DeleteMapping("/deleteHomeServiceRequest")
  public BaseResponseDTO<DeleteHomeServiceResponseDTO> deleteHomeServiceRequest(
      @RequestBody DeleteHomeServiceRequestDTO editHomeServiceRequestDTO) {
    log.info("Received request for delete home service request");
    var response = homeService.deleteHomeServiceRequest(editHomeServiceRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @PostMapping("/getAllUserServiceRequests")
  public BaseResponseDTO<GetAllHomeServiceResponseDTO> getAllUserServiceRequests() {
    log.info("Received request for getting all home service request for user");
    var response = homeService.getAllUserServiceRequests();
    return new BaseResponseDTO<>(response, null);
  }
}
