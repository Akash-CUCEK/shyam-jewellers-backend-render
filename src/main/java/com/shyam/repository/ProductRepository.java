package com.shyam.repository;

import com.shyam.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  Product findByProductName(String productName);
  boolean existsByProductName(String productName);
}