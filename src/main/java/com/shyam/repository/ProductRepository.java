package com.shyam.repository;

import com.shyam.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    boolean existsByProductName(String productName);

    Page<Product> findByStatusAndCategory_CategoryId(
            String status,
            Long categoryId,
            Pageable pageable
    );
}