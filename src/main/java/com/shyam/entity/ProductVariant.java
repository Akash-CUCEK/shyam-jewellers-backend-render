package com.shyam.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "product_variant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "variant_id")
  private Long variantId;

  @Column(name = "sku_code", nullable = false, unique = true)
  private String skuCode;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "weight", precision = 10, scale = 3)
  private BigDecimal weight;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "reserved_quantity", nullable = false)
  private Integer reservedQuantity;

  @Column(name = "status", nullable = false)
  private String status; // ACTIVE / INACTIVE / OUT_OF_STOCK

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "updated_by")
  private String updatedBy;
}
