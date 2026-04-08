package com.shyam.dao;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.dto.request.GetAllOrderRequestDTO;
import com.shyam.entity.Order;
import com.shyam.repository.OrderRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDAO {
  private final OrderRepository orderRepository;

  public Long countOrdersByOrderDateBetween(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
    try {
      log.debug("Getting total order of the month from {} to {}", firstDayOfMonth, lastDayOfMonth);
      return orderRepository.countOrdersByOrderDateBetween(firstDayOfMonth, lastDayOfMonth);

    } catch (Exception e) {
      log.error("Error while getting total order of the month", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          "Failed to get total orders of the month",
          e.getMessage());
    }
  }

  public Order findOrderByOrderId(Long orderId) {
    log.debug("Finding order by OrderId: {}", orderId);
    return orderRepository
        .findById(orderId)
        .orElseThrow(
            () ->
                new SYMException(
                    HttpStatus.NOT_FOUND,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_AUTHZ_USER_NOT_EXIST,
                    "Order with given orderId does not exist",
                    "Order with given orderId does not exist"));
  }

  public void save(Order addOrderRequestDTO) {
    try {
      log.debug("Saving the new order:");
      orderRepository.save(addOrderRequestDTO);
    } catch (Exception e) {
      log.error("Error while saving order:", e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to save new order"),
          e.getMessage());
    }
  }

  public List<Order> findOrderByOrderDate(GetAllOrderRequestDTO dto) {
    try {
      return orderRepository.findAllByOrderDate(dto.getOrderDate());
    } catch (Exception e) {
      log.error("Error while fetching orders by date: {}", dto.getOrderDate(), e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to fetch orders for date: %s", dto.getOrderDate()),
          e.getMessage());
    }
  }
}
