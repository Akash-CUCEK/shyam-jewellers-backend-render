package com.shyam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "material_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "material_type_id")
  private Long materialTypeId;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "status", nullable = false)
  private Boolean status;

  @Column(name = "making_charge_type", nullable = false)
  private String makingChargeType;

  @Column(name = "making_charge_value", precision = 10, scale = 2)
  private BigDecimal makingChargeValue;
}