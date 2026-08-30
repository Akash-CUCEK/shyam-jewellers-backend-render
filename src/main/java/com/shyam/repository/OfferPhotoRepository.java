package com.shyam.repository;

import com.shyam.entity.OfferPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface OfferPhotoRepository
        extends JpaRepository<OfferPhoto, Integer> {

  Optional<OfferPhoto> findByPosition(Integer position);

  List<OfferPhoto> findByPositionBetweenOrderByPosition(
          Integer startPosition,
          Integer endPosition
  );
}