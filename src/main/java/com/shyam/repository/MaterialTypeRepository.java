package com.shyam.repository;

import com.shyam.entity.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialTypeRepository extends JpaRepository<MaterialType, Long> {
  boolean existsByName(String name);

  MaterialType findByNameIgnoreCase(String name);
}
