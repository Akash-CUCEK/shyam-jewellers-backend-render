package com.shyam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "purity",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"material_type_id", "purity_name"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "purity_id")
  private Long purityId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
          name = "material_type_id",
          referencedColumnName = "material_type_id",
          nullable = false)
  private MaterialType materialType;

  @Column(name = "purity_name", nullable = false)
  private String purityName;

  @Column(name = "purity_factor", nullable = false, precision = 10, scale = 6)
  private BigDecimal purityFactor;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "status", nullable = false)
  private Boolean status;
}