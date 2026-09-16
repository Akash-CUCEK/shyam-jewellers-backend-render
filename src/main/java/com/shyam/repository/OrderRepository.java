package com.shyam.repository;

import com.shyam.common.constants.OrderStatus;
import com.shyam.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long>,
                JpaSpecificationExecutor<Order> {

    boolean existsByOrderNumber(String orderNumber);

    Page<Order> findByUser_UserId(Long userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderNumber LIKE :prefix")
    Long countByOrderNumberStartingWith(@Param("prefix") String prefix);

    /**
     * Find orders by status and createdAt less than the given dateTime.
     * @param status the order status
     * @param dateTime the dateTime threshold (orders created before this)
     * @return list of orders matching the criteria
     */
    List<Order> findByStatusAndCreatedAtLessThan(OrderStatus status, LocalDateTime dateTime);
}