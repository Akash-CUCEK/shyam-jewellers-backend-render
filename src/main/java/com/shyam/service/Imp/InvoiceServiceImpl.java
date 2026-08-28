package com.shyam.service.Imp;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.shyam.common.constants.OrderPaymentStatus;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dto.response.GetOrderInvoiceResponse;
import com.shyam.entity.Order;
import com.shyam.service.InvoiceService;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

  @Override
  public GetOrderInvoiceResponse generateInvoice(Order order) {
    byte[] pdfBytes = generateInvoicePdf(order);

    return GetOrderInvoiceResponse.builder()
        .invoicePdfBytes(pdfBytes)
        .fileName("invoice_" + order.getId() + ".pdf")
        .build();
  }

  @Override
  public byte[] generateInvoicePdf(Order order) {
    validatePaidOrder(order);

    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Document document = new Document();
      PdfWriter.getInstance(document, out);
      document.open();

      document.add(new Paragraph("***** Invoice *****"));
      document.add(new Paragraph("Generated On: " + LocalDateTime.now()));
      document.add(new Paragraph(" "));

      document.add(new Paragraph("Order ID: " + order.getId()));
      document.add(new Paragraph("Customer Name: " + order.getCustomerName()));
      document.add(new Paragraph("Customer Email: " + order.getCustomerEmail()));
      document.add(new Paragraph("Customer Phone: " + order.getCustomerPhone()));
      document.add(new Paragraph("Address: " + order.getAddress()));
      document.add(new Paragraph("Order Date: " + order.getOrderDate()));
      document.add(new Paragraph("Order Status: " + order.getOrderStatus()));
      document.add(new Paragraph("Payment Status: " + order.getPaymentStatus()));
      document.add(new Paragraph("Payment Method: " + order.getPaymentMethod()));
      document.add(new Paragraph("Total Cost: INR " + order.getTotalCost()));
      document.add(new Paragraph("Due Amount: INR " + order.getDueAmount()));

      document.close();
      return out.toByteArray();
    } catch (SYMException e) {
      throw e;
    } catch (Exception e) {
      log.error("Invoice generation failed", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Invoice generation failed",
          e.getMessage());
    }
  }

  private void validatePaidOrder(Order order) {
    if (order.getPaymentStatus() == OrderPaymentStatus.PAID) {
      return;
    }

    throw new SYMException(
        HttpStatus.BAD_REQUEST,
        SYMErrorType.USER_ERROR,
        ErrorCodeConstants.ERROR_CODE_VALIDATION,
        "Invoice can only be generated after successful payment",
        "Order " + order.getId() + " is not fully paid");
  }
}
