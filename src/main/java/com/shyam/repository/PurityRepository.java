package com.shyam.repository;

import com.shyam.entity.MaterialType;
import com.shyam.entity.Purity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurityRepository extends JpaRepository<Purity, Long> {
  Optional<Purity> findByMaterialTypeAndPurityName(MaterialType materialType, String purityName);

  boolean existsByMaterialTypeAndPurityName(MaterialType materialType, String purityName);

  List<Purity> findByMaterialType_MaterialTypeIdAndStatusTrue(Long materialTypeId);

  List<Purity> findByStatusTrue();
}
