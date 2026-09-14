package com.shyam.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
  @SequenceGenerator(
      name = "product_seq",
      sequenceName = "product_seq",
      allocationSize = 1)
  private Long productId;

  @Column(name = "product_name", unique = true)
  private String productName;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "material_type_id", referencedColumnName = "material_type_id", nullable = false)
  private MaterialType materialType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "purity_id", referencedColumnName = "purity_id", nullable = false)
  private Purity purity;

  @Column(name = "making_charge_type", nullable = false)
  private String makingChargeType; // e.g., "PERCENTAGE"

  @Column(name = "making_charge_value", precision = 10, scale = 2)
  private BigDecimal makingChargeValue;

  @Column(name = "description")
  private String description;

  @Column(name = "status", nullable = false)
  private String status; // DRAFT, ACTIVE, INACTIVE, OUT_OF_STOCK

  @Column(name = "hallmark_certified", nullable = false)
  private Boolean hallmarkCertified;

  @Column(name = "certification_number")
  private String certificationNumber;

  @Column(name = "discount_type")
  private String discountType; // FIXED, PERCENTAGE

  @Column(name = "discount_value", precision = 10, scale = 2)
  private BigDecimal discountValue;

  @Column(name = "discount_valid_till")
  private LocalDateTime discountValidTill;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "updated_by")
  private String updatedBy;
}