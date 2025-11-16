package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.OrderDto;
import com.innowise.orderservice.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * Service for managing {@link OrderDto} entities.
 * Extends basic CRUD operations and provides search with filters and pagination.
 */
public interface OrderService extends CrudService<OrderDto, Long> {

    /**
     * Searches orders using dynamic filters.
     *
     * @param userId         user ID filter
     * @param status         order status filter
     * @param createdAfter   creation date lower bound
     * @param createdBefore  creation date upper bound
     * @param pageable       pagination info
     * @return page of filtered orders
     */
    Page<OrderDto> searchOrders(Long userId,
                                String email,
                                String status,
                                LocalDateTime createdAfter,
                                LocalDateTime createdBefore,
                                Pageable pageable);

    OrderDto updateOrderStatus(Long orderId, OrderStatus status);
}
