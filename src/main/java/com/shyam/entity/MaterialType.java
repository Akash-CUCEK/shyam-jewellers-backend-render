package com.shyam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "material_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialType {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "material_type_seq")
  @SequenceGenerator(
      name = "material_type_seq",
      sequenceName = "material_type_seq",
      allocationSize = 1)
  private Long materialTypeId;

  @Column(nullable = false, unique = true)
  private String name;

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
