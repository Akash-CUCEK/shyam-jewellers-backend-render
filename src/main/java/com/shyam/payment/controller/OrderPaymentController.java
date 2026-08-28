package com.shyam.payment.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.payment.dto.response.PaymentResponseDTO;
import com.shyam.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Payment", description = "Order payment lookup endpoints")
public class OrderPaymentController {

  private final PaymentService paymentService;

  @Operation(summary = "Get order payment", description = "Gets the latest payment for an order.")
  @GetMapping("/{orderId}/payment")
  public BaseResponseDTO<PaymentResponseDTO> getOrderPayment(@PathVariable Long orderId) {
    return new BaseResponseDTO<>(paymentService.getLatestPaymentForOrder(orderId), null);
  }
}
