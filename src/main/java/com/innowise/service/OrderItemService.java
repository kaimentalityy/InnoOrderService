package com.innowise.service;

import com.innowise.model.dto.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service for managing {@link OrderItemDto} entities.
 */
public interface OrderItemService extends CrudService<OrderItemDto, Long> {

    Page<OrderItemDto> searchOrderItems(Long orderId,
                                        Long itemId,
                                        Integer quantity,
                                        Integer minQuantity,
                                        Integer maxQuantity,
                                        String jwtToken,
                                        Pageable pageable);
}
