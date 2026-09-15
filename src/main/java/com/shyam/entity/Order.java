package com.shyam.entity;

import com.shyam.common.constants.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import com.shyam.common.constants.OrderSource;
import com.shyam.common.constants.OrderPaymentStatus;

@Entity
@Table(name = "`order`", uniqueConstraints = @UniqueConstraint(columnNames = "order_number"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", referencedColumnName = "address_id", nullable = false)
    private Address address;

    @Column(name = "shipping_address_line1_snapshot")
    private String shippingAddressLine1Snapshot;

    @Column(name = "shipping_address_line2_snapshot")
    private String shippingAddressLine2Snapshot;

    @Column(name = "city_snapshot")
    private String citySnapshot;

    @Column(name = "state_snapshot")
    private String stateSnapshot;

    @Column(name = "pincode_snapshot")
    private String pincodeSnapshot;

    @Column(name = "phone_number_snapshot")
    private String phoneNumberSnapshot;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_source", nullable = false)
    private OrderSource orderSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private OrderPaymentStatus paymentStatus;

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