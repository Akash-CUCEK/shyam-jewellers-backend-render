package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.response.GetOfferPhotoResponseDTO;
import com.shyam.dto.response.GetPurityResponseDTO;
import com.shyam.service.OfferService;
import com.shyam.service.PurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
  private final PurityService purityService;

  @Operation(summary = "Get Offer Section", description = "Get list of available offer photos.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/getOfferPhoto")
  public BaseResponseDTO<List<GetOfferPhotoResponseDTO>> getoffer() {
    log.info("Entering getoffer method");
    log.info("Received request to get offer photo");
    var response = offerService.getOfferPhoto();
    log.info("Exiting getoffer method");
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get all active purities",
      description = "Get list of active purities, optionally filtered by material type.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/purities")
  public BaseResponseDTO<List<GetPurityResponseDTO>> getPublicPurities(
      @RequestParam(required = false) Long materialTypeId) {

    log.debug("Entering getPublicPurities method with materialTypeId: {}", materialTypeId);
    List<GetPurityResponseDTO> response =
        (materialTypeId != null)
            ? purityService.getPuritiesByMaterialType(materialTypeId)
            : purityService.getAllActivePurities();
    log.debug("Exiting getPublicPurities method");
    return new BaseResponseDTO<>(response, null);
  }
}
