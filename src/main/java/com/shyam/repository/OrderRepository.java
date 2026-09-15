package com.shyam.repository;

import com.shyam.common.constants.OrderStatus;
import com.shyam.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long>,
                JpaSpecificationExecutor<Order> {

    boolean existsByOrderNumber(String orderNumber);

    Page<Order> findByUser_UserId(Long userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}