package com.shyam.service;

import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import java.util.List;

/**
 * Service interface for offer photo management operations. Follows Single Responsibility Principle
 * by handling only offer-related concerns.
 */
public interface OfferService {

  /**
   * Updates or creates an offer photo at the specified position.
   *
   * @param editPhotoRequestDTO the offer photo details to update or create
   * @return response indicating the offer photo operation result
   */
  EditPhotoResponseDTO offerUpdate(EditPhotoRequestDTO editPhotoRequestDTO);

  /**
   * Retrieves all offer photos with their positions.
   *
   * @return list of offer photo response DTOs
   */
  List<GetOfferPhotoResponseDTO> getOfferPhoto();
}
