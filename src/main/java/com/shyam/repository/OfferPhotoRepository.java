package com.shyam.repository;

import com.shyam.entity.OfferPhoto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferPhotoRepository extends JpaRepository<OfferPhoto, Integer> {

  Optional<OfferPhoto> findByPosition(Integer position);

  List<OfferPhoto> findByPositionBetweenOrderByPosition(Integer startPosition, Integer endPosition);
}
