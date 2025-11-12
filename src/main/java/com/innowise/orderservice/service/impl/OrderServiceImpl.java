package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.dao.repository.OrderRepository;
import com.innowise.orderservice.dao.specification.OrderSpecifications;
import com.innowise.orderservice.event.OrderCreatedEvent;
import com.innowise.orderservice.event.OrderItemEvent;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.dto.OrderDto;
import com.innowise.orderservice.model.dto.UserInfoDto;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.service.kafka.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;
    private final OrderEventProducer orderEventProducer;

    @Override
    @Transactional
    public OrderDto create(OrderDto createDto) {
        Order order = orderMapper.toEntity(createDto);
        Order saved = orderRepository.save(order);

        sendOrderCreatedEvent(saved);

        return mapToOrderDto(saved, null);
    }

    @Override
    @Transactional
    public OrderDto update(Long id, OrderDto updateDto) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);

        orderMapper.updateEntity(existing, updateDto);
        Order updated = orderRepository.save(existing);
        return mapToOrderDto(updated, null);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException();
        }
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
        return mapToOrderDto(order, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> searchOrders(Long userId, String email, String status,
                                       LocalDateTime createdAfter, LocalDateTime createdBefore,
                                       Pageable pageable) {

        Specification<Order> spec = Specification.where(null);

        if (userId != null) spec = spec.and(OrderSpecifications.hasUserId(userId));
        if (status != null) spec = spec.and(OrderSpecifications.hasStatus(status));
        if (createdAfter != null) spec = spec.and(OrderSpecifications.createdAfter(createdAfter));
        if (createdBefore != null) spec = spec.and(OrderSpecifications.createdBefore(createdBefore));

        Page<Order> orders = orderRepository.findAll(spec, pageable);

        return orders.map(order -> mapToOrderDto(order, email));
    }

    private OrderDto mapToOrderDto(Order order, String email) {
        UserInfoDto userInfo = fetchUserInfo(order.getUserId(), email);
        return new OrderDto(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getCreatedDate(),
                orderMapper.orderItemsToDtos(order.getItems()),
                userInfo
        );
    }

    private UserInfoDto fetchUserInfo(Long userId, String email) {
        if (email != null && !email.isEmpty()) {
            return userServiceClient.getUserByEmail(email);
        }
        return userServiceClient.getUserById(userId);
    }

    /**
     * Send CREATE_ORDER event to Kafka
     */
    private void sendOrderCreatedEvent(Order order) {
        try {
            log.info("Preparing CREATE_ORDER event for order ID: {}", order.getId());

            BigDecimal totalAmount = calculateTotalAmount(order);

            List<OrderItemEvent> orderItemEvents = convertToOrderItemEvents(order.getItems());

            OrderCreatedEvent event = new OrderCreatedEvent(
                    order.getId(),
                    order.getUserId(),
                    order.getStatus(),
                    totalAmount,
                    orderItemEvents
            );

            orderEventProducer.sendOrderCreatedEvent(event);

            log.info("CREATE_ORDER event sent for order ID: {}", order.getId());

        } catch (Exception e) {
            log.error("Failed to send CREATE_ORDER event for order ID: {}", order.getId(), e);
        }
    }

    /**
     * Calculate total order amount from items
     */
    private BigDecimal calculateTotalAmount(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return order.getItems().stream()
                .map(this::calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate total for a single order item
     */
    private BigDecimal calculateItemTotal(OrderItem orderItem) {
        if (orderItem.getItem() == null || orderItem.getItem().getPrice() == null) {
            return BigDecimal.ZERO;
        }

        return orderItem.getItem().getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }

    /**
     * Convert OrderItem entities to OrderItemEvent DTOs
     */
    private List<OrderItemEvent> convertToOrderItemEvents(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return List.of();
        }

        return orderItems.stream()
                .map(orderItem -> new OrderItemEvent(
                        orderItem.getItem() != null ? orderItem.getItem().getId() : null,
                        orderItem.getItem() != null ? orderItem.getItem().getName() : "Unknown Item",
                        orderItem.getItem() != null ? orderItem.getItem().getPrice() : BigDecimal.ZERO,
                        orderItem.getQuantity() != null ? orderItem.getQuantity() : 0
                ))
                .toList();
    }
}