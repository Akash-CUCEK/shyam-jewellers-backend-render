package com.shyam.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_items_seq")
  @SequenceGenerator(name = "order_items_seq", sequenceName = "order_items_seq", allocationSize = 1)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  private Long productId;

  private Integer quantity;

  private BigDecimal price;
}
