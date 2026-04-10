package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.MESSAGE_CODE_CREATE_ORDER;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dao.OrderDAO;
import com.shyam.dto.request.AddOrderRequestDTO;
import com.shyam.dto.request.GetOrderByIdRequestDTO;
import com.shyam.dto.request.GetOrderInvoiceRequest;
import com.shyam.dto.request.UpdateOrderRequestDTO;
import com.shyam.dto.response.*;
import com.shyam.entity.Order;
import com.shyam.mapper.OrderMapper;
import com.shyam.repository.OrderRepository;
import com.shyam.service.OrderService;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

  private final OrderMapper orderMapper;
  private final MessageSourceUtil messageSourceUtil;
  private final OrderDAO orderDAO;
  private final OrderRepository orderRepository;

  /* ===================== CREATE ORDER ===================== */

  @Override
  public AddOrderResponseDTO createOrder(AddOrderRequestDTO requestDTO) {
    log.info("Creating new order");
    var order = orderMapper.mapToOrderEntity(requestDTO);
    orderDAO.save(order);
    log.info("Saved order");
    return orderMapper.mapToOrderCreateInMessage(
        messageSourceUtil.getMessage(MESSAGE_CODE_CREATE_ORDER));
  }

  @Override
  public AddOrderResponseDTO updateOrder(UpdateOrderRequestDTO dto) {
    log.info("Updating order with ID: {}", dto.getOrderId());
    Order order = orderDAO.findOrderByOrderId(dto.getOrderId());
    orderMapper.updateOrderEntity(order, dto);
    orderDAO.save(order);
    return AddOrderResponseDTO.builder().message("Order updated successfully").build();
  }

  /* ===================== GET ORDER BY ID ===================== */

  @Override
  public GetOrderByIdResponseDTO getOrderById(GetOrderByIdRequestDTO requestDTO) {
    log.info("Fetching order by ID");
    Order order = orderDAO.findOrderByOrderId(requestDTO.getOrderId());
    return orderMapper.mapToOrderResponseDTO(order);
  }

  /* ===================== GET ALL ORDERS ===================== */

  @Override
  public OrderListPageResponseDTO getAllOrders(int page, int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("updatedAt")));

    Page<Order> orderPage = orderRepository.findAll(pageable);

    List<OrderListResponseDTO> list =
        orderPage.getContent().stream().map(orderMapper::mapToOrderListDTO).toList();

    return OrderListPageResponseDTO.builder()
        .orders(list)
        .currentPage(orderPage.getNumber())
        .totalPages(orderPage.getTotalPages())
        .totalElements(orderPage.getTotalElements())
        .build();
  }

  /* ===================== TOTAL ORDERS THIS MONTH ===================== */

  @Override
  public GetTotalOrderMonthResponse getTotalOrderMonth() {

    LocalDate now = LocalDate.now();
    LocalDate firstDay = now.withDayOfMonth(1);
    LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

    Long count = orderDAO.countOrdersByOrderDateBetween(firstDay, lastDay);

    return GetTotalOrderMonthResponse.builder().count(count != null ? count : 0L).build();
  }

  /* ===================== INVOICE PDF ===================== */

  @Override
  public GetOrderInvoiceResponse getOrderInvoice(GetOrderInvoiceRequest request) {

    Order order = orderDAO.findOrderByOrderId(request.getOrderId());
    byte[] pdfBytes = generateInvoicePdf(order);

    return GetOrderInvoiceResponse.builder()
        .invoicePdfBytes(pdfBytes)
        .fileName("invoice_" + order.getId() + ".pdf")
        .build();
  }

  private byte[] generateInvoicePdf(Order order) {

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
      document.add(new Paragraph("Payment Method: " + order.getPaymentMethod()));
      document.add(new Paragraph("Total Cost: ₹" + order.getTotalCost()));
      document.add(new Paragraph("Due Amount: ₹" + order.getDueAmount()));

      document.close();
      return out.toByteArray();

    } catch (Exception e) {
      log.error("Invoice generation failed", e);
      throw new RuntimeException("Invoice generation failed", e);
    }
  }
}
