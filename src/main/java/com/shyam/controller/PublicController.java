package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.response.GetOfferPhotoResponseDTO;
import com.shyam.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

  private final AdminService adminService;

  @Operation(summary = "Get Offer Section", description = "Get offer photo.")
  @PostMapping("/getOfferPhoto")
  public BaseResponseDTO<GetOfferPhotoResponseDTO> getoffer() {
    log.info("Received request to get offer photo");
    var response = adminService.getOfferPhoto();
    return new BaseResponseDTO<>(response, null);
  }
}
