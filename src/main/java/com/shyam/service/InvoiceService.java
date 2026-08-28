package com.shyam.service;

import com.shyam.dto.response.GetOrderInvoiceResponse;
import com.shyam.entity.Order;

public interface InvoiceService {

  GetOrderInvoiceResponse generateInvoice(Order order);

  byte[] generateInvoicePdf(Order order);
}
