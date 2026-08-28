package com.shyam.repository;

import com.shyam.entity.RepairService;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairRequestRepository extends JpaRepository<RepairService, Long> {
  List<RepairService> findAllByOrderByCreatedAtDesc();

  @Query(
      "SELECT s FROM RepairService s "
          + "WHERE (:serviceId IS NOT NULL AND s.serviceId = :serviceId) "
          + "OR (:name IS NOT NULL AND LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))")
  List<RepairService> searchRepairRequests(
      @Param("serviceId") Long serviceId, @Param("name") String name);
}
