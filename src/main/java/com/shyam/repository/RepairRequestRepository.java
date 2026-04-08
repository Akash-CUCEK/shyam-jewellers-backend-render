package com.shyam.repository;

import com.shyam.entity.RepairService;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairRequestRepository extends JpaRepository<RepairService, Long> {
  List<RepairService> findAllByOrderByCreatedAtDesc();

  @Query(
      "SELECT s FROM ServiceHome s "
          + "WHERE (:serviceId IS NULL OR s.serviceId = :serviceId) "
          + "OR (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))")
  List<RepairService> searchRepairRequests(
      @Param("serviceId") Long serviceId, @Param("name") String name);
}
