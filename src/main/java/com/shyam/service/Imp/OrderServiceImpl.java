package com.shyam.service.Imp;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import com.shyam.common.constants.OrderStatus;
import com.shyam.common.constants.PaymentStatus;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;

import com.shyam.config.RazorpayConfig;

import com.shyam.dto.OrderResponseDTO;
import com.shyam.dto.PriceBreakdownDTO;

import com.shyam.entity.Address;
import com.shyam.entity.Order;
import com.shyam.entity.OrderItem;
import com.shyam.entity.Payment;
import com.shyam.entity.ProductVariant;
import com.shyam.entity.Users;

import com.shyam.repository.AddressRepository;
import com.shyam.repository.OrderItemRepository;
import com.shyam.repository.OrderRepository;
import com.shyam.repository.PaymentRepository;
import com.shyam.repository.ProductVariantRepository;
import com.shyam.repository.UsersRepository;

import com.shyam.service.OrderService;
import com.shyam.service.PricingService;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;
    private final PricingService pricingService;
    private final RazorpayConfig razorpayConfig;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            ProductVariantRepository productVariantRepository,
            AddressRepository addressRepository,
            UsersRepository usersRepository,
            PricingService pricingService,
            RazorpayConfig razorpayConfig) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.productVariantRepository = productVariantRepository;
        this.addressRepository = addressRepository;
        this.usersRepository = usersRepository;
        this.pricingService = pricingService;
        this.razorpayConfig = razorpayConfig;
    }

    @Override
    @Transactional
    public OrderService.OrderCheckoutResponse createOrder(
            List<OrderService.CartItem> cartItems,
            Long addressId,
            Long userId) {

        // Validate input
        if (cartItems == null || cartItems.isEmpty()) {
            throw new SYMException(
                    HttpStatus.BAD_REQUEST,
                    SYMErrorType.VALIDATION_FAILED,
                    "CART_EMPTY",
                    "Cart items cannot be empty",
                    "Cart items cannot be empty"
            );
        }

        if (addressId == null) {
            throw new SYMException(
                    HttpStatus.BAD_REQUEST,
                    SYMErrorType.VALIDATION_FAILED,
                    "ADDRESS_ID_NULL",
                    "Address ID is required",
                    "Address ID cannot be null"
            );
        }

        if (userId == null) {
            throw new SYMException(
                    HttpStatus.BAD_REQUEST,
                    SYMErrorType.VALIDATION_FAILED,
                    "USER_ID_NULL",
                    "User ID is required",
                    "User ID cannot be null"
            );
        }

        // Get address and validate it belongs to the user
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new SYMException(
                        HttpStatus.NOT_FOUND,
                        SYMErrorType.GENERIC_EXCEPTION,
                        "ADDRESS_NOT_FOUND",
                        "Address not found with id: " + addressId,
                        "Address not found"
                ));

        // Validate address ownership
        if (!Objects.equals(address.getUser().getUserId(), userId)) {
            throw new SYMException(
                    HttpStatus.FORBIDDEN,
                    SYMErrorType.VALIDATION_FAILED,
                    "ADDRESS_ACCESS_DENIED",
                    "Address does not belong to the user",
                    "Address with id: " + addressId
                            + " does not belong to user with id: " + userId
            );
        }

        // Get user
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new SYMException(
                        HttpStatus.NOT_FOUND,
                        SYMErrorType.GENERIC_EXCEPTION,
                        "USER_NOT_FOUND",
                        "User not found with id: " + userId,
                        "User not found"
                ));

        // Calculate total amount and prepare order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        for (OrderService.CartItem cartItem : cartItems) {

            Long variantId = cartItem.getVariantId();
            Integer quantity = cartItem.getQuantity();

            if (variantId == null || quantity == null || quantity <= 0) {
                throw new SYMException(
                        HttpStatus.BAD_REQUEST,
                        SYMErrorType.VALIDATION_FAILED,
                        "INVALID_CART_ITEM",
                        "Invalid cart item",
                        "Cart item must have valid variantId and positive quantity"
                );
            }

            // Use atomic stock reservation instead of read-then-write
            Integer updatedRows =
                    productVariantRepository.incrementReservedQuantity(
                            variantId,
                            quantity
                    );

            if (updatedRows == 0) {

                // Fetch variant details for error message
                ProductVariant variant =
                        productVariantRepository.findById(variantId)
                                .orElseThrow(() -> new SYMException(
                                        HttpStatus.NOT_FOUND,
                                        SYMErrorType.GENERIC_EXCEPTION,
                                        "VARIANT_NOT_FOUND",
                                        "Product variant not found with id: "
                                                + variantId,
                                        "Product variant not found"
                                ));

                Integer availableStock =
                        variant.getQuantity()
                                - (variant.getReservedQuantity() != null
                                ? variant.getReservedQuantity()
                                : 0);

                throw new SYMException(
                        HttpStatus.BAD_REQUEST,
                        SYMErrorType.VALIDATION_FAILED,
                        "INSUFFICIENT_STOCK",
                        "Insufficient stock for SKU: "
                                + variant.getSkuCode(),
                        "Requested quantity: " + quantity
                                + ", Available stock: "
                                + availableStock
                );
            }

            // Fetch the variant again to get fresh data for pricing
            ProductVariant variant =
                    productVariantRepository.findById(variantId)
                            .orElseThrow(() -> new SYMException(
                                    HttpStatus.NOT_FOUND,
                                    SYMErrorType.GENERIC_EXCEPTION,
                                    "VARIANT_NOT_FOUND",
                                    "Product variant not found with id: "
                                            + variantId,
                                    "Product variant not found"
                            ));

            // Calculate price using PricingService
            PriceBreakdownDTO priceBreakdown =
                    pricingService.calculatePrice(variant);

            // Update total amount
            totalAmount = totalAmount.add(
                    priceBreakdown.getFinalPrice()
                            .multiply(BigDecimal.valueOf(quantity))
            );

            // Create order item with snapshots from price breakdown
            OrderItem orderItem = new OrderItem();

            orderItem.setProductVariant(variant);
            orderItem.setQuantity(quantity);
            orderItem.setSkuCodeSnapshot(variant.getSkuCode());
            orderItem.setProductNameSnapshot(
                    variant.getProduct().getProductName()
            );

            // Fixed type mismatch: setter expects BigDecimal
            orderItem.setWeightSnapshot(variant.getWeight());

            orderItem.setMetalValueSnapshot(
                    priceBreakdown.getMetalValue()
            );

            orderItem.setMakingChargeSnapshot(
                    priceBreakdown.getMakingCharge()
            );

            orderItem.setGstSnapshot(
                    priceBreakdown.getGst()
            );

            orderItem.setFinalPriceSnapshot(
                    priceBreakdown.getFinalPrice()
            );

            orderItem.setCreatedAt(LocalDateTime.now());
            orderItem.setCreatedBy(user.getEmail());

            orderItemsToSave.add(orderItem);
        }

        // Generate order number with proper sequential numbering
        String orderNumber = generateOrderNumber();

        // Create order entity
        Order order = new Order();

        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setAddress(address);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setCreatedBy(user.getEmail());

        // updatedAt and updatedBy will be set on update

        Order savedOrder = orderRepository.save(order);

        // Set order reference in order items and save them
        for (OrderItem orderItem : orderItemsToSave) {

            orderItem.setOrder(savedOrder);

            orderItemRepository.save(orderItem);
        }

        // Create Razorpay order
        String razorpayOrderId =
                createRazorpayOrder(totalAmount);

        // Create payment entity
        Payment payment = new Payment();

        payment.setOrder(savedOrder);
        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setCreatedBy(user.getEmail());

        Payment savedPayment =
                paymentRepository.save(payment);

        // Return response
        OrderService.OrderCheckoutResponse response =
                new OrderService.OrderCheckoutResponse();

        response.setOrderId(savedOrder.getOrderId());
        response.setOrderNumber(savedOrder.getOrderNumber());
        response.setTotalAmount(savedOrder.getTotalAmount());
        response.setRazorpayOrderId(
                savedPayment.getRazorpayOrderId()
        );
        response.setRazorpayAmount(
                savedPayment.getAmount()
        );

        return response;
    }

    // Helper method to generate order number
    // Format: ORD-{year}-{sequentialNumber}
    private String generateOrderNumber() {

        int year = LocalDateTime.now().getYear();

        String prefix = "ORD-" + year + "-";

        // Get count of existing orders for this year
        Long count =
                orderRepository.countByOrderNumberStartingWith(prefix);

        // Increment count and format as 5-digit zero-padded number
        int sequenceNumber = count.intValue() + 1;

        String sequenceStr =
                String.format("%05d", sequenceNumber);

        return prefix + sequenceStr;
    }

    private String createRazorpayOrder(BigDecimal amount) {

        try {

            // Initialize Razorpay client with API credentials
            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            razorpayConfig.getKeyId(),
                            razorpayConfig.getKeySecret()
                    );

            // Create order request
            JSONObject orderRequest = new JSONObject();

            // Amount should be in paise (INR * 100)
            orderRequest.put(
                    "amount",
                    amount.multiply(new BigDecimal("100"))
                            .intValue()
            );

            orderRequest.put("currency", "INR");

            // Temporary receipt value
            orderRequest.put(
                    "receipt",
                    "order_rcpt_" + System.currentTimeMillis()
            );

            // Create the order via Razorpay API
            //
            // IMPORTANT:
            // This is Razorpay Order, not com.shyam.entity.Order.
            com.razorpay.Order razorpayOrder =
                    razorpayClient.orders.create(orderRequest);

            // Return Razorpay order ID
            return razorpayOrder.get("id");

        } catch (RazorpayException e) {

            System.err.println(
                    "Razorpay order creation failed: "
                            + e.getMessage()
            );

            throw new SYMException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    SYMErrorType.GENERIC_EXCEPTION,
                    "RAZORPAY_ORDER_CREATION_FAILED",
                    "Failed to create Razorpay order: "
                            + e.getMessage(),
                    "Razorpay order creation failed"
            );
        }
    }

    @Override
    @Transactional
    public Payment verifyPayment(
            Long orderId,
            String razorpayPaymentId,
            String razorpayOrderId,
            String razorpaySignature) {

        // Get the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new SYMException(
                        HttpStatus.NOT_FOUND,
                        SYMErrorType.GENERIC_EXCEPTION,
                        "ORDER_NOT_FOUND",
                        "Order not found with id: " + orderId,
                        "Order not found"
                ));

        // Get the payment for this order
        Payment payment =
                paymentRepository.findByOrder_OrderId(orderId);

        if (payment == null) {
            throw new SYMException(
                    HttpStatus.NOT_FOUND,
                    SYMErrorType.GENERIC_EXCEPTION,
                    "PAYMENT_NOT_FOUND",
                    "Payment not found for order ID: "
                            + orderId,
                    "Payment not found"
            );
        }

        // Verify the signature
        boolean isValidSignature =
                verifyRazorpaySignature(
                        razorpayOrderId,
                        razorpayPaymentId,
                        razorpaySignature
                );

        if (isValidSignature) {

            // Payment successful
            payment.setRazorpayPaymentId(
                    razorpayPaymentId
            );

            payment.setRazorpaySignature(
                    razorpaySignature
            );

            payment.setStatus(
                    PaymentStatus.SUCCESS
            );

            payment.setUpdatedAt(
                    LocalDateTime.now()
            );

            payment.setUpdatedBy(
                    payment.getCreatedBy()
            );

            order.setStatus(
                    OrderStatus.CONFIRMED
            );

            order.setUpdatedAt(
                    LocalDateTime.now()
            );

            order.setUpdatedBy(
                    order.getCreatedBy()
            );

            // Deduct stock:
            // decrease total quantity and reserved quantity
            for (OrderItem item : order.getOrderItems()) {

                ProductVariant variant =
                        item.getProductVariant();

                Integer itemQuantity =
                        item.getQuantity();

                // Decrease total quantity
                Integer newQuantity =
                        variant.getQuantity()
                                - itemQuantity;

                if (newQuantity < 0) {

                    throw new SYMException(
                            HttpStatus.BAD_REQUEST,
                            SYMErrorType.VALIDATION_FAILED,
                            "INSUFFICIENT_TOTAL_STOCK",
                            "Insufficient total stock for variant: "
                                    + variant.getSkuCode(),
                            "Attempted to deduct quantity: "
                                    + itemQuantity
                    );
                }

                variant.setQuantity(newQuantity);

                // Decrease reserved quantity
                Integer newReserved =
                        variant.getReservedQuantity() != null
                                ? variant.getReservedQuantity()
                                : 0;

                newReserved =
                        newReserved - itemQuantity;

                if (newReserved < 0) {

                    throw new SYMException(
                            HttpStatus.BAD_REQUEST,
                            SYMErrorType.VALIDATION_FAILED,
                            "RESERVED_QUANTITY_NEGATIVE",
                            "Reserved quantity cannot be negative for variant: "
                                    + variant.getSkuCode(),
                            "Attempted to deduct quantity: "
                                    + itemQuantity
                    );
                }

                variant.setReservedQuantity(
                        newReserved
                );

                productVariantRepository.save(
                        variant
                );
            }

        } else {

            // Payment failed
            payment.setStatus(
                    PaymentStatus.FAILED
            );

            payment.setUpdatedAt(
                    LocalDateTime.now()
            );

            payment.setUpdatedBy(
                    payment.getCreatedBy()
            );

            order.setStatus(
                    OrderStatus.FAILED
            );

            order.setUpdatedAt(
                    LocalDateTime.now()
            );

            order.setUpdatedBy(
                    order.getCreatedBy()
            );

            // Release reserved quantity
            for (OrderItem item : order.getOrderItems()) {

                ProductVariant variant =
                        item.getProductVariant();

                Integer itemQuantity =
                        item.getQuantity();

                Integer newReserved =
                        variant.getReservedQuantity() != null
                                ? variant.getReservedQuantity()
                                : 0;

                newReserved =
                        newReserved - itemQuantity;

                if (newReserved < 0) {

                    throw new SYMException(
                            HttpStatus.BAD_REQUEST,
                            SYMErrorType.VALIDATION_FAILED,
                            "RESERVED_QUANTITY_NEGATIVE",
                            "Reserved quantity cannot be negative for variant: "
                                    + variant.getSkuCode(),
                            "Attempted to deduct quantity: "
                                    + itemQuantity
                    );
                }

                variant.setReservedQuantity(
                        newReserved
                );

                productVariantRepository.save(
                        variant
                );
            }
        }

        // Save payment and order
        Payment savedPayment =
                paymentRepository.save(payment);

        orderRepository.save(order);

        return savedPayment;
    }

    private boolean verifyRazorpaySignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String signature) {

        try {

            // Initialize Razorpay client with API credentials
            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            razorpayConfig.getKeyId(),
                            razorpayConfig.getKeySecret()
                    );

            // Payload format:
            // order_id + "|" + payment_id
            String payload =
                    razorpayOrderId
                            + "|"
                            + razorpayPaymentId;

            boolean isValidSignature =
                    com.razorpay.Utils.verifySignature(
                            payload,
                            signature,
                            razorpayConfig.getKeySecret()
                    );

            return isValidSignature;

        } catch (RazorpayException e) {

            System.err.println(
                    "Razorpay signature verification failed: "
                            + e.getMessage()
            );

            // Treat verification error as invalid signature
            return false;
        }
    }

    @Override
    public Page<OrderResponseDTO> getOrdersByUser(
            Long userId,
            Pageable pageable) {

        Page<Order> ordersPage =
                orderRepository.findByUser_UserId(
                        userId,
                        pageable
                );

        List<OrderResponseDTO> orderDTOs =
                ordersPage.getContent()
                        .stream()
                        .map(OrderResponseDTO::fromEntity)
                        .collect(Collectors.toList());

        return new PageImpl<>(
                orderDTOs,
                pageable,
                ordersPage.getTotalElements()
        );
    }

    @Override
    public OrderResponseDTO getOrderById(Long orderId) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() -> new SYMException(
                                HttpStatus.NOT_FOUND,
                                SYMErrorType.GENERIC_EXCEPTION,
                                "ORDER_NOT_FOUND",
                                "Order not found with id: "
                                        + orderId,
                                "Order not found"
                        ));

        return OrderResponseDTO.fromEntity(order);
    }

    @Override
    public Order cancelOrder(
            Long orderId,
            String updatedBy) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() -> new SYMException(
                                HttpStatus.NOT_FOUND,
                                SYMErrorType.GENERIC_EXCEPTION,
                                "ORDER_NOT_FOUND",
                                "Order not found with id: "
                                        + orderId,
                                "Order not found"
                        ));

        // Only allow cancellation if status is
        // PENDING or CONFIRMED
        if (!order.getStatus().equals(OrderStatus.PENDING)
                && !order.getStatus().equals(OrderStatus.CONFIRMED)) {

            throw new SYMException(
                    HttpStatus.BAD_REQUEST,
                    SYMErrorType.VALIDATION_FAILED,
                    "ORDER_CANCEL_NOT_ALLOWED",
                    "Order cannot be cancelled in current status: "
                            + order.getStatus(),
                    "Order can only be cancelled when status is PENDING or CONFIRMED"
            );
        }

        // Set order status to CANCELLED
        order.setStatus(
                OrderStatus.CANCELLED
        );

        order.setUpdatedAt(
                LocalDateTime.now()
        );

        order.setUpdatedBy(
                updatedBy
        );

        // Release reservedQuantity for all order items
        for (OrderItem item : order.getOrderItems()) {

            ProductVariant variant =
                    item.getProductVariant();

            Integer currentReserved =
                    variant.getReservedQuantity() != null
                            ? variant.getReservedQuantity()
                            : 0;

            variant.setReservedQuantity(
                    currentReserved - item.getQuantity()
            );

            productVariantRepository.save(
                    variant
            );
        }

        return orderRepository.save(order);
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(
            Pageable pageable,
            String status,
            String startDate,
            String endDate) {

        Specification<Order> spec =
                Specification.where(null);

        // Apply status filter if provided
        if (status != null && !status.isEmpty()) {

            try {

                OrderStatus orderStatus =
                        OrderStatus.valueOf(
                                status.toUpperCase()
                        );

                spec = spec.and(
                        (root, query, cb) ->
                                cb.equal(
                                        root.get("status"),
                                        orderStatus
                                )
                );

            } catch (IllegalArgumentException e) {

                throw new SYMException(
                        HttpStatus.BAD_REQUEST,
                        SYMErrorType.VALIDATION_FAILED,
                        "INVALID_STATUS",
                        "Invalid order status: " + status,
                        "Valid statuses are: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, FAILED"
                );
            }
        }

        // Apply startDate filter if provided
        if (startDate != null && !startDate.isEmpty()) {

            try {

                LocalDateTime startDateTime =
                        LocalDateTime.parse(startDate);

                spec = spec.and(
                        (root, query, cb) ->
                                cb.greaterThanOrEqualTo(
                                        root.get("createdAt"),
                                        startDateTime
                                )
                );

            } catch (Exception e) {

                throw new SYMException(
                        HttpStatus.BAD_REQUEST,
                        SYMErrorType.VALIDATION_FAILED,
                        "INVALID_START_DATE",
                        "Invalid start date format: "
                                + startDate,
                        "Expected format: yyyy-MM-dd'T'HH:mm:ss"
                );
            }
        }

        // Apply endDate filter if provided
        if (endDate != null && !endDate.isEmpty()) {

            try {

                LocalDateTime endDateTime =
                        LocalDateTime.parse(endDate);

                spec = spec.and(
                        (root, query, cb) ->
                                cb.lessThanOrEqualTo(
                                        root.get("createdAt"),
                                        endDateTime
                                )
                );

            } catch (Exception e) {

                throw new SYMException(
                        HttpStatus.BAD_REQUEST,
                        SYMErrorType.VALIDATION_FAILED,
                        "INVALID_END_DATE",
                        "Invalid end date format: "
                                + endDate,
                        "Expected format: yyyy-MM-dd'T'HH:mm:ss"
                );
            }
        }

        Page<Order> ordersPage =
                orderRepository.findAll(
                        spec,
                        pageable
                );

        List<OrderResponseDTO> orderDTOs =
                ordersPage.getContent()
                        .stream()
                        .map(OrderResponseDTO::fromEntity)
                        .collect(Collectors.toList());

        return new PageImpl<>(
                orderDTOs,
                pageable,
                ordersPage.getTotalElements()
        );
    }

    @Override
    public Order updateOrderStatus(
            Long orderId,
            String status,
            String updatedBy) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() -> new SYMException(
                                HttpStatus.NOT_FOUND,
                                SYMErrorType.GENERIC_EXCEPTION,
                                "ORDER_NOT_FOUND",
                                "Order not found with id: "
                                        + orderId,
                                "Order not found"
                        ));

        try {

            order.setStatus(
                    OrderStatus.valueOf(
                            status.toUpperCase()
                    )
            );

        } catch (IllegalArgumentException e) {

            throw new SYMException(
                    HttpStatus.BAD_REQUEST,
                    SYMErrorType.VALIDATION_FAILED,
                    "INVALID_STATUS",
                    "Invalid status: " + status,
                    "Valid statuses are: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, FAILED"
            );
        }

        order.setUpdatedAt(
                LocalDateTime.now()
        );

        order.setUpdatedBy(
                updatedBy
        );

        return orderRepository.save(order);
    }
}