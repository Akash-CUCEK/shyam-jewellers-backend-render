package com.shyam.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "purity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purity_seq")
  @SequenceGenerator(name = "purity_seq", sequenceName = "purity_seq", allocationSize = 1)
  private Long purityId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "material_type_id",
      referencedColumnName = "material_type_id",
      nullable = false)
  private MaterialType materialType;

  @Column(nullable = false, unique = false)
  private String purityName;

  @Column(name = "purity_factor", nullable = false, precision = 10, scale = 6)
  private BigDecimal purityFactor;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(nullable = false)
  private Boolean status;
}
