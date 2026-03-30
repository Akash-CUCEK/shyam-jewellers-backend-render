package com.shyam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
    @SequenceGenerator(name = "products_seq", sequenceName = "products_seq", allocationSize = 1)
    private Long productIds;

    @Column(unique = true)
    private String name;

    // 🔥 Correct relation
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private BigDecimal price;

    private Integer discountPercentage;

    @Column(nullable = false)
    private BigDecimal weight;

    @Column(nullable = false)
    private String materialType;

    @Column(nullable = false, unique = true)
    private String skuCode;

    @Column(length = 200)
    private String shortDescription;

    @Lob
    private String fullDescription;

    private String gender;

    private Double averageRating;

    private Boolean isAvailable;

    private Integer quantity;

    private Integer availableStock;

    @Column(name = "image_url")
    private String imageUrl;

    private String createdBy;
    private String updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.skuCode = "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}