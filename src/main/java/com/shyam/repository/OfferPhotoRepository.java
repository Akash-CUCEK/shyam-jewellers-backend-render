package com.shyam.repository;

import com.shyam.entity.OfferPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferPhotoRepository extends JpaRepository<OfferPhoto, Integer> {
  OfferPhoto findTopByOrderByCreatedAtDesc();
}
