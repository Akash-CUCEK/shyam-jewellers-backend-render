package com.shyam.repository;

import com.shyam.entity.Order;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findAllByOrderDate(LocalDate orderDate);

  @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
  Long countOrdersByOrderDateBetween(
      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
