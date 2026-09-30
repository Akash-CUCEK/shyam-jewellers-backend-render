package com.shyam.repository;

import com.shyam.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductVariantRepository
    extends JpaRepository<ProductVariant, Long>, JpaSpecificationExecutor<ProductVariant> {

  @Transactional
  @Modifying
  @Query(
      "UPDATE ProductVariant pv SET pv.reservedQuantity = pv.reservedQuantity + :quantity WHERE pv.variantId = :variantId AND (pv.quantity - pv.reservedQuantity) >= :quantity")
  int incrementReservedQuantity(Long variantId, Integer quantity);
}
