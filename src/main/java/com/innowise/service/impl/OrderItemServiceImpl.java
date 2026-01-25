package com.innowise.service.impl;

import com.innowise.dao.repository.OrderItemRepository;
import com.innowise.dao.specification.OrderItemSpecifications;
import com.innowise.exception.OrderItemNotFoundException;
import com.innowise.mapper.OrderItemMapper;
import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.entity.OrderItem;
import com.innowise.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderItemDto create(OrderItemDto createDto, String jwtToken) {
        OrderItem saved = orderItemRepository.save(orderItemMapper.toEntity(createDto));
        return orderItemMapper.toDto(saved);
    }

    @Override
    public OrderItemDto update(Long id, OrderItemDto updateDto, String jwtToken) {
        OrderItem existing = orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException());
        orderItemMapper.updateEntity(existing, updateDto);
        return orderItemMapper.toDto(orderItemRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!orderItemRepository.existsById(id)) {
            throw new OrderItemNotFoundException();
        }
        orderItemRepository.deleteById(id);
    }

    @Override
    public OrderItemDto findById(Long id, String jwtToken) {
        return orderItemRepository.findById(id)
                .map(orderItemMapper::toDto)
                .orElseThrow(() -> new OrderItemNotFoundException());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemDto> searchOrderItems(Long orderId, Long itemId, Integer quantity,
                                               Integer minQuantity, Integer maxQuantity,
                                               String jwtToken, Pageable pageable) {
        Specification<OrderItem> spec = Specification.where(null);
        if (orderId != null) spec = spec.and(OrderItemSpecifications.hasOrderId(orderId));
        if (itemId != null) spec = spec.and(OrderItemSpecifications.hasItemId(itemId));
        if (quantity != null) spec = spec.and(OrderItemSpecifications.hasQuantity(quantity));
        if (minQuantity != null) spec = spec.and(OrderItemSpecifications.quantityGreaterThan(minQuantity));
        if (maxQuantity != null) spec = spec.and(OrderItemSpecifications.quantityLessThan(maxQuantity));
        return orderItemRepository.findAll(spec, pageable)
                .map(orderItemMapper::toDto);
    }
}

