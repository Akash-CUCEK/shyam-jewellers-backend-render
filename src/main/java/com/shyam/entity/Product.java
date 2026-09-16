package com.shyam.entity;

import com.shyam.common.constants.ProductStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = "product_name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long productId;

  @Column(name = "product_name", nullable = false, unique = true)
  private String productName;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "material_type_id", nullable = false)
  private MaterialType materialType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "purity_id", nullable = false)
  private Purity purity;

  @Column(name = "making_charge_type", nullable = false)
  private String makingChargeType;

  @Column(name = "making_charge_value", nullable = false, precision = 10, scale = 2)
  private BigDecimal makingChargeValue;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ProductStatus status;

  @Column(name = "hallmark_certified", nullable = false)
  private Boolean hallmarkCertified;

  @Column(name = "certification_number", length = 100)
  private String certificationNumber;

  @Column(name = "discount_type", length = 20)
  private String discountType;

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
