package com.shyam.service.Imp;

import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dao.AdminDAO;
import com.shyam.dto.request.EditPhotoRequestDTO;
import com.shyam.dto.response.EditPhotoResponseDTO;
import com.shyam.dto.response.GetOfferPhotoResponseDTO;
import com.shyam.entity.OfferPhoto;
import com.shyam.mapper.AdminMapper;
import com.shyam.service.OfferService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferServiceImp implements OfferService {

  private final AdminDAO adminDAO;
  private final AdminMapper adminMapper;
  private final MessageSourceUtil messageSourceUtil;

  @Override
  @Transactional
  public EditPhotoResponseDTO offerUpdate(EditPhotoRequestDTO request) {

    log.info("========================================");
    log.info("Processing to save offer section");
    log.info("Position    : {}", request.getPosition());
    log.info("Available   : {}", request.getIsAvailable());
    log.info("Image URL   : {}", request.getImgUrl());
    log.info("========================================");

    if (request.getPosition() == null) {
      throw new IllegalArgumentException("Offer position is required");
    }

    if (request.getPosition() < 1 || request.getPosition() > 5) {
      throw new IllegalArgumentException("Offer position must be between 1 and 5");
    }

    OfferPhoto offer = adminDAO.getPhotoByPosition(request.getPosition());

    LocalDateTime now = LocalDateTime.now();

    if (offer == null) {

      log.info("No offer found at position {}. Creating new offer.", request.getPosition());

      offer = new OfferPhoto();

      offer.setPosition(request.getPosition());
      offer.setCreatedAt(now);

    } else {

      log.info("Existing offer found. ID: {}. Updating offer.", offer.getId());
    }

    offer.setImgUrl(request.getImgUrl());
    offer.setIsAvailable(request.getIsAvailable());
    offer.setUpdatedAt(now);

    adminDAO.saveOffer(offer);

    log.info("Offer saved successfully. Position: {}", request.getPosition());

    return adminMapper.mapToEditPhotoRequestDTOAdminInMessage(
        messageSourceUtil.getMessage(
            com.shyam.constants.MessageConstant.MESSAGE_CODE_UPDATE_OFFER_ADMIN));
  }

  @Override
  @Transactional(readOnly = true)
  public List<GetOfferPhotoResponseDTO> getOfferPhoto() {
    log.info("Getting offer photos");
    List<OfferPhoto> offerPhotos = adminDAO.getPhotosWithPosition();
    if (offerPhotos.isEmpty()) {
      log.info("No available offer photo found in DB.");
      return Collections.emptyList();
    }
    return offerPhotos.stream()
        .map(
            offer ->
                GetOfferPhotoResponseDTO.builder()
                    .imgUrl(offer.getImgUrl())
                    .position(offer.getPosition())
                    .build())
        .collect(Collectors.toList());
  }
}
