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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @JoinColumn(name = "order_id", referencedColumnName = "order_id")   // "orderId" → "order_id"
    private Order order;

    @JoinColumn(name = "product_variant_id", referencedColumnName = "variant_id")  // "variantId" → "variant_id"
    private ProductVariant productVariant;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "sku_code_snapshot")
    private String skuCodeSnapshot;

    @Column(name = "product_name_snapshot")
    private String productNameSnapshot;

    @Column(name = "weight_snapshot", precision = 10, scale = 3)
    private BigDecimal weightSnapshot;

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