package com.shyam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "offer_photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferPhoto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "img_url")
  private String imgUrl;

  @Column(name = "is_available")
  private Boolean isAvailable;

  @Column(name = "position")
  private Integer position;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
