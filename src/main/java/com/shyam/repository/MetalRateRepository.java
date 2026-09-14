package com.shyam.repository;

import com.shyam.entity.MetalRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface MetalRateRepository extends JpaRepository<MetalRate, Long> {

  @Query("SELECT mr FROM MetalRate mr WHERE mr.materialType = :materialType ORDER BY mr.fetchedAt DESC")
  Optional<MetalRate> findLatestByMaterialType(@Param("materialType") com.shyam.entity.MaterialType materialType);
}