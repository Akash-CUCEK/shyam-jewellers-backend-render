package com.shyam.entity;

import com.shyam.common.constants.OrderPaymentStatus;
import com.shyam.common.constants.OrderStatus;
import com.shyam.common.constants.PaymentMethod;
import com.shyam.common.constants.Role;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
  @SequenceGenerator(name = "orders_seq", sequenceName = "orders_seq", allocationSize = 1)
  private Long id;

  private String customerName;
  private String customerEmail;
  private String customerPhone;
  private String address;

  private LocalDate orderDate;
  private LocalTime orderTime;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  private String deliveryType;

  private BigDecimal totalCost;
  private BigDecimal dueAmount;

  @Enumerated(EnumType.STRING)
  private OrderPaymentStatus paymentStatus;

  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  private String notes;

  private String createdBy;

  @Enumerated(EnumType.STRING)
  private Role createdByRole;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> items;
}
