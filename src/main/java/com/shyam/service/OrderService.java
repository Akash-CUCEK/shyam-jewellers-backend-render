package com.shyam.service;

import com.shyam.entity.Order;
import com.shyam.entity.OrderItem;
import com.shyam.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    /**
     * Create a new order from cart items.
     * @param cartItems List of objects containing variantId and quantity
     * @param addressId The ID of the address to use for shipping
     * @param userId The ID of the user placing the order
     * @return An object containing the order details and Razorpay order information
     */
    OrderCheckoutResponse createOrder(List<CartItem> cartItems, Long addressId, Long userId);

    /**
     * Verify payment and update order and payment status.
     * @param orderId The ID of the order
     * @param razorpayPaymentId The payment ID from Razorpay
     * @param razorpayOrderId The order ID from Razorpay
     * @param razorpaySignature The signature from Razorpay
     * @return The updated payment entity
     */
    Payment verifyPayment(Long orderId, String razorpayPaymentId, String razorpayOrderId, String razorpaySignature);

    /**
     * Get orders for a specific user.
     * @param userId The ID of the user
     * @param pageable Pagination information
     * @return Page of orders
     */
    Page<Order> getOrdersByUser(Long userId, Pageable pageable);

    /**
     * Get an order by its ID.
     * @param orderId The ID of the order
     * @return The order entity
     */
    Order getOrderById(Long orderId);

    /**
     * Cancel an order if it is in a cancellable state.
     * @param orderId The ID of the order to cancel
     * @return The updated order entity
     */
    Order cancelOrder(Long orderId);

    // Admin methods
    Page<Order> getAllOrders(Pageable pageable, String status, String startDate, String endDate);
    Order updateOrderStatus(Long orderId, String status);
}

// DTO for cart item
class CartItem {
    private Long variantId;
    private Integer quantity;

    // Getters and setters
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

// DTO for order checkout response
class OrderCheckoutResponse {
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String razorpayOrderId;
    private BigDecimal razorpayAmount;
    // Add other fields as needed

    // Getters and setters
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