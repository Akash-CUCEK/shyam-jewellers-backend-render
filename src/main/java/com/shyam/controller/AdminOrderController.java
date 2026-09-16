package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.OrderResponseDTO;
import com.shyam.entity.Order;
import com.shyam.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Admin controller for order management.
 */
@Slf4j
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin Order", description = "Admin order management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all orders with filtering", description = "Retrieve a paginated list of all orders with optional status and date filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient role", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public BaseResponseDTO<Page<OrderResponseDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Received request for getting all orders, page: {}, size: {}, status: {}, startDate: {}, endDate: {}",
                page, size, status, startDate, endDate);
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponseDTO> orders = orderService.getAllOrders(pageable, status,
                startDate != null ? startDate.toString() : null,
                endDate != null ? endDate.toString() : null);
        log.info("Successfully retrieved all orders, page: {}, size: {}", page, size);
        return new BaseResponseDTO<>(orders, null);
    }

    @Operation(summary = "Get order by ID", description = "Retrieve an order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient role", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{orderId}")
    public BaseResponseDTO<OrderResponseDTO> getOrderById(@PathVariable Long orderId) {
        log.info("Received request for getting order by id: {}", orderId);
        OrderResponseDTO order = orderService.getOrderById(orderId);
        log.info("Successfully retrieved order by id: {}", orderId);
        return new BaseResponseDTO<>(order, null);
    }

    @Operation(summary = "Update order status", description = "Update the status of an order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status or unable to update", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient role", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{orderId}/status")
    public BaseResponseDTO<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestParam String updatedBy) {
        log.info("Received request to update order id: {} status to: {} by: {}", orderId, status, updatedBy);
        Order order = orderService.updateOrderStatus(orderId, status, updatedBy);
        log.info("Successfully updated order id: {} status to: {}", orderId, status);
        return new BaseResponseDTO<>(order, null);
    }
}