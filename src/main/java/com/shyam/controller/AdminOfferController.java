package com.shyam.controller;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dto.request.EditPhotoRequestDTO;
import com.shyam.dto.response.EditPhotoResponseDTO;
import com.shyam.service.Imp.CloudinaryService;
import com.shyam.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/api/v1/admin")
@Tag(name = "Admin Offer", description = "Admin offer management endpoints")
public class AdminOfferController {

  private final OfferService offerService;
  private final CloudinaryService cloudinaryService;

  @Operation(summary = "Offer Section", description = "Adding offer photo.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Offer photo updated successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @PostMapping(value = "/addOfferPhoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<EditPhotoResponseDTO> offerUpdate(
      @RequestParam("position") Integer position,
      @RequestParam("image") MultipartFile image,
      @RequestParam("isAvailable") Boolean isAvailable) {
    log.info(
        "Entering offerUpdate method with position: {}, image: {}, isAvailable: {}",
        position,
        image,
        isAvailable);
    log.info("Received request for offer update");
    if (image == null || image.isEmpty()) {
      log.error("Offer image is empty");
      throw new SYMException(
          HttpStatus.BAD_REQUEST,
          SYMErrorType.VALIDATION_FAILED,
          ErrorCodeConstants.ERROR_CODE_VALIDATION,
          "Offer image is required",
          "Offer image is empty");
    }

    String imageUrl = cloudinaryService.upload(image);
    log.info("Image uploaded to Cloudinary, url: {}", imageUrl);

    EditPhotoRequestDTO request =
        EditPhotoRequestDTO.builder()
            .imgUrl(imageUrl)
            .position(position)
            .isAvailable(isAvailable)
            .build();

    var response = offerService.offerUpdate(request);
    log.info("Offer photo updated successfully");
    log.info("Exiting offerUpdate method");
    return new BaseResponseDTO<>(response, null);
  }
}
