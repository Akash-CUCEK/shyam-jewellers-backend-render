package com.shyam.controller;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.constants.ErrorCodeConstants;

import com.shyam.dto.OrderResponseDTO;

import com.shyam.entity.Order;
import com.shyam.entity.Payment;
import com.shyam.entity.Users;

import com.shyam.repository.UsersRepository;
import com.shyam.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(
        name = "Order",
        description = "User-facing order endpoints"
)
public class OrderController {

    private final OrderService orderService;
    private final UsersRepository usersRepository;


    /**
     * Helper method to get the current user ID from JWT token.
     * Assumes the JWT token contains the user's email as the username.
     */
    private Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new SYMException(
                    HttpStatus.UNAUTHORIZED,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND_BY_MAIL,
                    "User not authenticated",
                    "User not authenticated"
            );
        }

        Object principal = authentication.getPrincipal();

        String email;

        if (principal instanceof UserDetails) {

            email = ((UserDetails) principal)
                    .getUsername();

        } else {

            email = principal.toString();
        }

        Users user =
                usersRepository.findByEmail(email)
                        .orElseThrow(() -> new SYMException(
                                HttpStatus.NOT_FOUND,
                                SYMErrorType.GENERIC_EXCEPTION,
                                ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND_BY_MAIL,
                                "User not found with email: " + email,
                                "User not found"
                        ));

        return user.getUserId();
    }


    // ============================================================
    // CREATE ORDER / CHECKOUT
    // ============================================================

    @Operation(
            summary = "Create a new order from cart",
            description = "Create a new order for the authenticated user."
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Order created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponseDTO.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient role",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @PostMapping("/checkout")
    public ResponseEntity<
            BaseResponseDTO<OrderService.OrderCheckoutResponse>
            > checkout(
            @Valid @RequestBody
            List<OrderService.CartItem> cartItems,

            @RequestParam Long addressId) {

        log.info(
                "Received request to create order for user with addressId: {}",
                addressId
        );

        Long userId = getCurrentUserId();

        OrderService.OrderCheckoutResponse response =
                orderService.createOrder(
                        cartItems,
                        addressId,
                        userId
                );

        log.info(
                "Order created successfully for userId: {}",
                userId
        );

        return ResponseEntity.ok(
                new BaseResponseDTO<>(
                        response,
                        null
                )
        );
    }


    // ============================================================
    // VERIFY PAYMENT
    // ============================================================

    @Operation(
            summary = "Verify payment for an order",
            description = "Verify Razorpay payment and update order status."
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Payment verified successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponseDTO.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payment details or verification failed",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient role",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @PostMapping("/{orderId}/verify-payment")
    public ResponseEntity<BaseResponseDTO<?>> verifyPayment(
            @PathVariable Long orderId,

            @Valid
            @RequestBody VerifyPaymentRequest request) {

        log.info(
                "Received request to verify payment for orderId: {}",
                orderId
        );

        Long userId = getCurrentUserId();

        /*
         * Note:
         * The service method verifyPayment does not currently
         * check ownership.
         *
         * Existing logic is preserved:
         * 1. Call verifyPayment
         * 2. Get order from returned payment
         * 3. Check ownership
         */

        Payment payment =
                orderService.verifyPayment(
                        orderId,
                        request.getRazorpayPaymentId(),
                        request.getRazorpayOrderId(),
                        request.getRazorpaySignature()
                );

        // Check ownership
        Order order = payment.getOrder();

        if (!order.getUser()
                .getUserId()
                .equals(userId)) {

            throw new SYMException(
                    HttpStatus.FORBIDDEN,
                    SYMErrorType.VALIDATION_FAILED,
                    ErrorCodeConstants.MESSAGE_CODE_ERROR_CODE_ORDER_NOT_FOUND,
                    "Order does not belong to the user",
                    "Order with id: " + orderId
                            + " does not belong to user with id: "
                            + userId
            );
        }

        log.info(
                "Payment verified successfully for orderId: {}",
                orderId
        );

        return ResponseEntity.ok(
                new BaseResponseDTO<>(
                        payment,
                        null
                )
        );
    }


    // ============================================================
    // VERIFY PAYMENT REQUEST DTO
    // ============================================================

    static class VerifyPaymentRequest {

        private String razorpayPaymentId;

        private String razorpayOrderId;

        private String razorpaySignature;


        public String getRazorpayPaymentId() {
            return razorpayPaymentId;
        }

        public void setRazorpayPaymentId(
                String razorpayPaymentId) {

            this.razorpayPaymentId =
                    razorpayPaymentId;
        }


        public String getRazorpayOrderId() {
            return razorpayOrderId;
        }

        public void setRazorpayOrderId(
                String razorpayOrderId) {

            this.razorpayOrderId =
                    razorpayOrderId;
        }


        public String getRazorpaySignature() {
            return razorpaySignature;
        }

        public void setRazorpaySignature(
                String razorpaySignature) {

            this.razorpaySignature =
                    razorpaySignature;
        }
    }


    // ============================================================
    // GET USER ORDERS
    // ============================================================

    @Operation(
            summary = "Get all orders for the authenticated user",
            description = "Retrieve a paginated list of orders for the authenticated user."
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponseDTO.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient role",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<
            BaseResponseDTO<
                    org.springframework.data.domain.Page<OrderResponseDTO>
                    >
            > getOrders(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        log.info(
                "Received request to get orders for user, page: {}, size: {}",
                page,
                size
        );

        Long userId = getCurrentUserId();

        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(
                        page,
                        size
                );

        org.springframework.data.domain.Page<OrderResponseDTO> orders =
                orderService.getOrdersByUser(
                        userId,
                        pageable
                );

        log.info(
                "Successfully retrieved orders for userId: {}, page: {}, size: {}",
                userId,
                page,
                size
        );

        return ResponseEntity.ok(
                new BaseResponseDTO<>(
                        orders,
                        null
                )
        );
    }


    // ============================================================
    // GET ORDER BY ID
    // ============================================================

    @Operation(
            summary = "Get order by ID for the authenticated user",
            description = "Retrieve an order by its ID for the authenticated user (ownership check applied)."
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponseDTO.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient role or order does not belong to user",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<
            BaseResponseDTO<OrderResponseDTO>
            > getOrderById(
            @PathVariable Long orderId) {

        log.info(
                "Received request to get order by id: {} for current user",
                orderId
        );

        Long userId = getCurrentUserId();

        OrderResponseDTO order =
                orderService.getOrderById(orderId);

        // Ownership check
        if (!order.getUserId().equals(userId)) {

            throw new SYMException(
                    HttpStatus.FORBIDDEN,
                    SYMErrorType.VALIDATION_FAILED,
                    ErrorCodeConstants.ERROR_CODE_ORDER_NOT_FOUND,
                    "Order does not belong to the user",
                    "Order with id: " + orderId
                            + " does not belong to user with id: "
                            + userId
            );
        }

        log.info(
                "Successfully retrieved order by id: {} for userId: {}",
                orderId,
                userId
        );

        return ResponseEntity.ok(
                new BaseResponseDTO<>(
                        order,
                        null
                )
        );
    }


    // ============================================================
    // CANCEL ORDER
    // ============================================================

    @Operation(
            summary = "Cancel an order for the authenticated user",
            description = "Cancel an order by its ID for the authenticated user (ownership and status checks applied)."
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Order cancelled successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponseDTO.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Order cannot be cancelled due to its status",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient role or order does not belong to user",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<BaseResponseDTO<?>> cancelOrder(
            @PathVariable Long orderId) {

        log.info(
                "Received request to cancel orderId: {}",
                orderId
        );

        Long userId = getCurrentUserId();

        // First get the order to check ownership
        OrderResponseDTO order =
                orderService.getOrderById(orderId);

        if (!order.getUserId().equals(userId)) {

            throw new SYMException(
                    HttpStatus.FORBIDDEN,
                    SYMErrorType.VALIDATION_FAILED,
                    ErrorCodeConstants.ERROR_CODE_ORDER_NOT_FOUND,
                    "Order does not belong to the user",
                    "Order with id: " + orderId
                            + " does not belong to user with id: "
                            + userId
            );
        }

        // Cancel the order
        Order cancelledOrder =
                orderService.cancelOrder(
                        orderId,
                        order.getCreatedBy()
                );

        log.info(
                "Order cancelled successfully for orderId: {} by userId: {}",
                orderId,
                userId
        );

        return ResponseEntity.ok(
                new BaseResponseDTO<>(
                        cancelledOrder,
                        null
                )
        );
    }
}