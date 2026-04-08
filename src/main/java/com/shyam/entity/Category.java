package com.shyam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
  @SequenceGenerator(name = "category_seq", sequenceName = "category_seq", allocationSize = 1)
  private Long categoryId;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(name = "show_on_home", nullable = false)
  private Boolean showOnHome;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

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
