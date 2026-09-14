package com.shyam.repository;

import com.shyam.entity.Purity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurityRepository extends JpaRepository<Purity, Long> {
  Purity findByMaterialTypeAndPurityName(MaterialType materialType, String purityName);
  boolean existsByMaterialTypeAndPurityName(MaterialType materialType, String purityName);
}