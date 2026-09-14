package com.shyam.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "metal_rate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetalRate {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metal_rate_seq")
  @SequenceGenerator(name = "metal_rate_seq", sequenceName = "metal_rate_seq", allocationSize = 1)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "material_type_id", nullable = false)
  private MaterialType materialType;

  @Column(name = "rate_per_gram", nullable = false, precision = 10, scale = 4)
  private BigDecimal ratePerGram;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency = "INR";

  @Column(name = "unit", nullable = false, length = 10)
  private String unit = "g";

  @Column(name = "source", nullable = false, length = 50)
  private String source;

  @Column(name = "fetched_at", nullable = false)
  private LocalDateTime fetchedAt;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_by", updatable = false)
  private String createdBy;
}
