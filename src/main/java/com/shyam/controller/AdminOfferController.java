package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.EditPhotoRequestDTO;
import com.shyam.dto.response.EditPhotoResponseDTO;
import com.shyam.service.Imp.CloudinaryService;
import com.shyam.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AdminOfferController {

    private final OfferService offerService;
    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Offer Section", description = "Adding offer photo.")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping(value = "/addOfferPhoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponseDTO<EditPhotoResponseDTO> offerUpdate(
            @RequestParam("position") Integer position,
            @RequestParam("image") MultipartFile image,
            @RequestParam("isAvailable") Boolean isAvailable) {
        log.info("Received request for offer update");
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Offer image is empty");
        }

        String imageUrl = cloudinaryService.upload(image);

        EditPhotoRequestDTO request =
                EditPhotoRequestDTO.builder()
                        .imgUrl(imageUrl)
                        .position(position)
                        .isAvailable(isAvailable)
                        .build();

        var response = offerService.offerUpdate(request);
        return new BaseResponseDTO<>(response, null);
    }
}
