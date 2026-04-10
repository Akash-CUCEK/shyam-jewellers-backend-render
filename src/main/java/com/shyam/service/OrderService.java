package com.shyam.service;

import com.shyam.dto.request.AddOrderRequestDTO;
import com.shyam.dto.request.GetOrderByIdRequestDTO;
import com.shyam.dto.request.GetOrderInvoiceRequest;
import com.shyam.dto.request.UpdateOrderRequestDTO;
import com.shyam.dto.response.*;

public interface OrderService {
  AddOrderResponseDTO createOrder(AddOrderRequestDTO addOrderRequestDTO);

  AddOrderResponseDTO updateOrder(UpdateOrderRequestDTO updateOrderRequestDTO);

  GetOrderByIdResponseDTO getOrderById(GetOrderByIdRequestDTO getOrderByIdRequestDTO);

  OrderListPageResponseDTO getAllOrders(int page, int size);

  GetTotalOrderMonthResponse getTotalOrderMonth();

  GetOrderInvoiceResponse getOrderInvoice(GetOrderInvoiceRequest getOrderInvoiceRequest);
}
