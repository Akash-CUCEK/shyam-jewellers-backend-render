package com.shyam.service;

import java.math.BigDecimal;

// DTO for order checkout response
public class OrderCheckoutResponse {
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