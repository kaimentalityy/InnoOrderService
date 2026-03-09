package com.innowise.service;

import com.innowise.model.dto.OrderDto;
import com.innowise.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderService extends CrudService<OrderDto, Long> {

    Page<OrderDto> searchOrders(String userId,
            String email,
            String status,
            LocalDateTime createdAfter,
            LocalDateTime createdBefore,
            Pageable pageable);

    OrderDto updateOrderStatus(Long orderId, OrderStatus status);
}
