package com.shyam.repository;

import com.shyam.entity.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialTypeRepository extends JpaRepository<MaterialType, Long> {
  MaterialType findByName(String name);

  boolean existsByName(String name);
}
