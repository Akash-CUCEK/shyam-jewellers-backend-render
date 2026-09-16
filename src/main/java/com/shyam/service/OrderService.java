package com.shyam.service;

import com.shyam.dto.OrderResponseDTO;
import com.shyam.entity.Order;
import com.shyam.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for managing orders.
 */
public interface OrderService {

    /**
     * Create a new order from cart items.
     *
     * @param cartItems List of objects containing variantId and quantity
     * @param addressId The ID of the address to use for shipping
     * @param userId The ID of the user placing the order
     * @return An object containing the order details and Razorpay order information
     */
    OrderCheckoutResponse createOrder(
            List<CartItem> cartItems,
            Long addressId,
            Long userId
    );

    /**
     * Verify payment and update order and payment status.
     *
     * @param orderId The ID of the order
     * @param razorpayPaymentId The payment ID from Razorpay
     * @param razorpayOrderId The order ID from Razorpay
     * @param razorpaySignature The signature from Razorpay
     * @return The updated payment entity
     */
    Payment verifyPayment(
            Long orderId,
            String razorpayPaymentId,
            String razorpayOrderId,
            String razorpaySignature
    );

    /**
     * Get orders for a specific user.
     *
     * @param userId The ID of the user
     * @param pageable Pagination information
     * @return Page of order response DTOs
     */
    Page<OrderResponseDTO> getOrdersByUser(
            Long userId,
            Pageable pageable
    );

    /**
     * Get an order by its ID.
     *
     * @param orderId The ID of the order
     * @return The order response DTO
     */
    OrderResponseDTO getOrderById(Long orderId);

    /**
     * Cancel an order if it is in a cancellable state.
     *
     * @param orderId The ID of the order to cancel
     * @param updatedBy The user ID performing the cancellation
     * @return The updated order entity
     */
    Order cancelOrder(
            Long orderId,
            String updatedBy
    );

    /**
     * Get all orders for admin.
     */
    Page<OrderResponseDTO> getAllOrders(
            Pageable pageable,
            String status,
            String startDate,
            String endDate
    );

    /**
     * Update order status for admin.
     */
    Order updateOrderStatus(
            Long orderId,
            String status,
            String updatedBy
    );

    /**
     * Cart item used during order creation.
     */
    class CartItem {

        private Long variantId;
        private Integer quantity;

        public CartItem() {
        }

        public CartItem(Long variantId, Integer quantity) {
            this.variantId = variantId;
            this.quantity = quantity;
        }

        public Long getVariantId() {
            return variantId;
        }

        public void setVariantId(Long variantId) {
            this.variantId = variantId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    /**
     * Response returned after creating an order.
     *
     * Contains the application order information
     * and Razorpay order information.
     */
    class OrderCheckoutResponse {

        private Long orderId;
        private String orderNumber;
        private BigDecimal totalAmount;
        private String razorpayOrderId;
        private BigDecimal razorpayAmount;

        public OrderCheckoutResponse() {
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getRazorpayOrderId() {
            return razorpayOrderId;
        }

        public void setRazorpayOrderId(String razorpayOrderId) {
            this.razorpayOrderId = razorpayOrderId;
        }

        public BigDecimal getRazorpayAmount() {
            return razorpayAmount;
        }

        public void setRazorpayAmount(BigDecimal razorpayAmount) {
            this.razorpayAmount = razorpayAmount;
        }
    }
}