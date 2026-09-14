package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.response.GetOfferPhotoResponseDTO;
import com.shyam.dto.response.GetPurityResponseDTO;
import com.shyam.service.OfferService;
import com.shyam.service.PurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/v1/public")
@Tag(name = "Public", description = "Publicly accessible endpoints")
public class PublicController {

  private final OfferService offerService;
  private final PurityService purityService;   // constructor injection me add karo

  @Operation(summary = "Get Offer Section", description = "Get list of available offer photos.")
  @PostMapping("/getOfferPhoto")
  public BaseResponseDTO<List<GetOfferPhotoResponseDTO>> getoffer() {
    log.info("Received request to get offer photo");
    var response = offerService.getOfferPhoto();
    return new BaseResponseDTO<>(response, null);
  }

    @Operation(summary = "Get all active purities", description = "Get list of active purities, optionally filtered by material type.")
    @GetMapping("/purities")
    public BaseResponseDTO<List<GetPurityResponseDTO>> getPublicPurities(
            @RequestParam(required = false) Long materialTypeId) {

      List<GetPurityResponseDTO> response = (materialTypeId != null)
              ? purityService.getPuritiesByMaterialType(materialTypeId)
              : purityService.getAllActivePurities();

      return new BaseResponseDTO<>(response, null);
    }
}

