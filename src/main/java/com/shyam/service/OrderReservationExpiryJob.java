package com.shyam.service;

import com.shyam.common.constants.OrderStatus;
import com.shyam.entity.Order;
import com.shyam.entity.OrderItem;
import com.shyam.entity.ProductVariant;
import com.shyam.repository.OrderRepository;
import com.shyam.repository.ProductVariantRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job to expire pending orders that have been pending for more than 15 minutes. Releases
 * the reserved quantity for all order items in such orders.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReservationExpiryJob {

  private final OrderRepository orderRepository;
  private final ProductVariantRepository productVariantRepository;

  /** Runs every 5 minutes to find and expire pending orders older than 15 minutes. */
  @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
  public void expirePendingOrders() {
    log.info("Starting job to expire pending orders older than 15 minutes");
    LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
    List<Order> pendingOldOrders =
        orderRepository.findByStatusAndCreatedAtLessThan(OrderStatus.PENDING, fifteenMinutesAgo);
    log.info("Found {} pending orders older than 15 minutes", pendingOldOrders.size());

    for (Order order : pendingOldOrders) {
      try {
        // Update order status to FAILED
        order.setStatus(OrderStatus.FAILED);
        order.setUpdatedAt(LocalDateTime.now());
        // We don't have a user to set as updatedBy for system action, so we can set it to "SYSTEM"
        // or leave null.
        // Let's set it to "SYSTEM"
        order.setUpdatedBy("SYSTEM");
        orderRepository.save(order);

        // Release reserved quantity for all order items
        for (OrderItem item : order.getOrderItems()) {
          ProductVariant variant = item.getProductVariant();
          Integer quantityToRelease = item.getQuantity();
          // Use the atomic update to decrement reserved quantity
          Integer updatedRows =
              productVariantRepository.incrementReservedQuantity(
                  variant.getVariantId(), -quantityToRelease);
          if (updatedRows == 0) {
            log.warn(
                "Failed to release reserved quantity for variantId: {} (orderId: {}) - variant not found",
                variant.getVariantId(),
                order.getOrderId());
          } else {
            log.debug(
                "Released reserved quantity of {} for variantId: {} (orderId: {})",
                quantityToRelease,
                variant.getVariantId(),
                order.getOrderId());
          }
        }

        log.info("Order {} expired and reserved quantity released", order.getOrderId());
      } catch (Exception e) {
        log.error("Error processing order {} for expiry", order.getOrderId(), e);
        // Continue with other orders
      }
    }

    log.info("Job to expire pending orders completed");
  }
}
