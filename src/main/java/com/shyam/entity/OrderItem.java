package com.shyam.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_seq", allocationSize = 1)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "orderId")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", referencedColumnName = "variantId")
    private ProductVariant productVariant;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "sku_code_snapshot")
    private String skuCodeSnapshot;

    @Column(name = "product_name_snapshot")
    private String productNameSnapshot;

    @Column(name = "weight_snapshot")
    private String weightSnapshot;

    @Column(name = "metal_value_snapshot", precision = 10, scale = 2)
    private BigDecimal metalValueSnapshot;

    @Column(name = "making_charge_snapshot", precision = 10, scale = 2)
    private BigDecimal makingChargeSnapshot;

    @Column(name = "gst_snapshot", precision = 10, scale = 2)
    private BigDecimal gstSnapshot;

    @Column(name = "final_price_snapshot", precision = 10, scale = 2)
    private BigDecimal finalPriceSnapshot;

    // Audit fields
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}