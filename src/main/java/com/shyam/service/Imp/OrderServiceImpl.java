//package com.shyam.service.Imp;
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.shyam.common.util.MessageSourceUtil;
//import com.shyam.dao.OrderDAO;
//import com.shyam.dto.request.AddOrderRequestDTO;
//import com.shyam.dto.request.GetOrderByIdRequestDTO;
//import com.shyam.dto.request.GetOrderInvoiceRequest;
//import com.shyam.dto.request.UpdateOrderRequestDTO;
//import com.shyam.dto.response.*;
//import com.shyam.entity.Order;
//import com.shyam.mapper.OrderMapper;
//import com.shyam.repository.OrderRepository;
//import com.shyam.service.OrderService;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static com.shyam.constants.MessageConstant.MESSAGE_CODE_CREATE_ORDER;
//
//@Service
//@RequiredArgsConstructor
//public class OrderServiceImpl implements OrderService {
//
//    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
//
//    private final OrderMapper orderMapper;
//    private final MessageSourceUtil messageSourceUtil;
//    private final OrderDAO orderDAO;
//    private final OrderRepository orderRepository;
//
//    /* ===================== CREATE ORDER ===================== */
//
//    @Override
//    public AddOrderResponseDTO createOrder(AddOrderRequestDTO requestDTO) {
//        logger.info("Creating new order");
//        Order order = orderMapper.mapToOrderEntity(requestDTO);
//        orderDAO.save(order);
//        logger.info("Saved order");
//        return orderMapper.mapToOrderCreateInMessage(
//                messageSourceUtil.getMessage(MESSAGE_CODE_CREATE_ORDER)
//        );
//    }
//
//    /* ===================== GET ORDER BY ID ===================== */
//
//    @Override
//    public GetOrderByIdResponseDTO getOrderById(GetOrderByIdRequestDTO requestDTO) {
//
//        logger.info("Fetching order by ID");
//
//        Order order = orderDAO.findOrderByOrderId(requestDTO.getOrderId());
//        return orderMapper.mapToOrderResponseDTO(order);
//    }
//
//    /* ===================== GET ALL ORDERS ===================== */
//
//    @Override
//    public GetAllOrderResponseDTO getAllOrders() {
//
//        logger.info("Fetching all orders");
//
//        List<Order> orders = orderRepository.findAll();
//
//        List<GetOrderByIdResponseDTO> responseList = orders.stream()
//                .map(orderMapper::mapToOrderResponseDTO)
//                .toList();
//
//        return GetAllOrderResponseDTO.builder()
//                .getOrderByDateResponseDTOList(responseList)
//                .build();
//    }
//    @Override
//    public AddOrderResponseDTO updateOrder(UpdateOrderRequestDTO updateOrderRequestDTO) {
//        logger.info("Updating order with ID: {}", updateOrderRequestDTO.getOrderId());
//        Order order = orderDAO.findOrderByOrderId(updateOrderRequestDTO.getOrderId());
//        if (order == null) {
//            throw new RuntimeException(
//                    "Order not found with id: " + updateOrderRequestDTO.getOrderId()
//            );
//        }
//        orderMapper.updateOrderEntity(order, updateOrderRequestDTO);
//        orderDAO.save(order);
//
//        return AddOrderResponseDTO.builder()
//                .message("Order updated successfully")
//                .build();
//    }
//
//
//    /* ===================== TOTAL ORDERS THIS MONTH ===================== */
//
//    @Override
//    public GetTotalOrderMonthResponse getTotalOrderMonth() {
//
//        LocalDate now = LocalDate.now();
//        LocalDate firstDay = now.withDayOfMonth(1);
//        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
//
//        Long count = orderDAO.countOrdersByOrderDateBetween(firstDay, lastDay);
//
//        return GetTotalOrderMonthResponse.builder()
//                .count(count != null ? count : 0L)
//                .build();
//    }
//
//    /* ===================== INVOICE PDF ===================== */
//
//    @Override
//    public GetOrderInvoiceResponse getOrderInvoice(GetOrderInvoiceRequest request) {
//
//        Order order = orderDAO.findOrderByOrderId(request.getOrderId());
//        byte[] pdfBytes = generateInvoicePdf(order);
//
//        return GetOrderInvoiceResponse.builder()
//                .invoicePdfBytes(pdfBytes)
//                .fileName("invoice_" + order.getId() + ".pdf")
//                .build();
//    }
//
//    private byte[] generateInvoicePdf(Order order) {
//
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//
//            Document document = new Document();
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            document.add(new Paragraph("***** Invoice *****"));
//            document.add(new Paragraph("Generated On: " + LocalDateTime.now()));
//            document.add(new Paragraph(" "));
//
//            document.add(new Paragraph("Order ID: " + order.getId()));
//            document.add(new Paragraph("Customer Name: " + order.getCustomerName()));
//            document.add(new Paragraph("Customer Email: " + order.getCustomerEmail()));
//            document.add(new Paragraph("Customer Phone: " + order.getCustomerPhone()));
//            document.add(new Paragraph("Address: " + order.getAddress()));
//            document.add(new Paragraph("Order Date: " + order.getOrderDate()));
//            document.add(new Paragraph("Order Status: " + order.getOrderStatus()));
//            document.add(new Paragraph("Payment Method: " + order.getPaymentMethod()));
//            document.add(new Paragraph("Total Cost: ₹" + order.getTotalCost()));
//            document.add(new Paragraph("Due Amount: ₹" + order.getDueAmount()));
//
//            document.close();
//            return out.toByteArray();
//
//        } catch (Exception e) {
//            logger.error("Invoice generation failed", e);
//            throw new RuntimeException("Invoice generation failed", e);
//        }
//    }
//}
