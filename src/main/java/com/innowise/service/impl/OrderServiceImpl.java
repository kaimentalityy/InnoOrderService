package com.innowise.service.impl;

import com.innowise.client.UserServiceClient;
import com.innowise.dao.repository.ItemRepository;
import com.innowise.dao.repository.OrderRepository;
import com.innowise.dao.specification.OrderSpecifications;
import com.innowise.event.OrderCreatedEvent;
import com.innowise.event.OrderItemEvent;
import com.innowise.exception.OrderNotFoundException;
import com.innowise.mapper.OrderMapper;
import com.innowise.model.dto.OrderDto;
import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.dto.UserInfoDto;
import com.innowise.model.entity.Item;
import com.innowise.model.entity.Order;
import com.innowise.model.entity.OrderItem;
import com.innowise.model.enums.OrderStatus;
import com.innowise.service.OrderService;
import com.innowise.service.kafka.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ItemRepository itemRepository;
    private final UserServiceClient userServiceClient;
    private final OrderEventProducer orderEventProducer;

    @Override
    @Transactional
    public OrderDto create(OrderDto createDto, String jwtToken) {
        Order order = orderMapper.toEntity(createDto);
        order.setCreatedDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PAYMENT_PENDING);

        List<OrderItem> orderItems = new ArrayList<>();

        if (createDto.items() != null) {
            for (OrderItemDto dto : createDto.items()) {

                Item item = itemRepository.findById(dto.itemId())
                        .orElseThrow(() -> new RuntimeException("Item not found"));

                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setItem(item);
                oi.setQuantity(dto.quantity());

                orderItems.add(oi);
            }
        }

        order.setItems(orderItems);
        Order saved = orderRepository.save(order);
        sendOrderCreatedEvent(saved);

        return mapToOrderDto(saved, null, jwtToken);
    }

    @Override
    @Transactional
    public OrderDto update(Long id, OrderDto updateDto, String jwtToken) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);

        orderMapper.updateEntity(existing, updateDto);
        Order updated = orderRepository.save(existing);

        return mapToOrderDto(updated, null, jwtToken);
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
    public OrderDto findById(Long id, String jwtToken) {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);

        return mapToOrderDto(order, null, jwtToken);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> searchOrders(Long userId, String email, String status,
                                       LocalDateTime createdAfter, LocalDateTime createdBefore,
                                       String jwtToken, Pageable pageable) {

        Specification<Order> spec = Specification.where(null);

        if (userId != null) spec = spec.and(OrderSpecifications.hasUserId(userId));
        if (status != null) spec = spec.and(OrderSpecifications.hasStatus(status));
        if (createdAfter != null) spec = spec.and(OrderSpecifications.createdAfter(createdAfter));
        if (createdBefore != null) spec = spec.and(OrderSpecifications.createdBefore(createdBefore));

        return orderRepository.findAll(spec, pageable)
                .map(order -> mapToOrderDto(order, email, jwtToken));
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status, String jwtToken) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        order.setStatus(status);
        Order saved = orderRepository.save(order);

        return mapToOrderDto(saved, null, jwtToken);
    }

    private OrderDto mapToOrderDto(Order order, String email, String jwtToken) {
        UserInfoDto userInfo = fetchUserInfo(order.getUserId(), email, jwtToken);
        return new OrderDto(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getCreatedDate(),
                orderMapper.orderItemsToDtos(order.getItems()),
                userInfo
        );
    }

    private UserInfoDto fetchUserInfo(Long userId, String email, String jwtToken) {
        if (email != null && !email.isEmpty()) {
            return userServiceClient.getUserByEmail(email, jwtToken);
        }
        return userServiceClient.getUserById(userId, jwtToken);
    }

    private void sendOrderCreatedEvent(Order order) {
        log.info("Preparing CREATE_ORDER event for order ID: {}", order.getId());

        BigDecimal totalAmount = calculateTotalAmount(order);
        List<OrderItemEvent> items = convertToOrderItemEvents(order.getItems());

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(totalAmount)
                .items(items)
                .build();

        orderEventProducer.sendOrderCreatedEvent(event);

        log.info("CREATE_ORDER event sent for order ID: {}", order.getId());
    }

    private BigDecimal calculateTotalAmount(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) return BigDecimal.ZERO;

        return order.getItems().stream()
                .map(this::calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemTotal(OrderItem item) {
        if (item.getItem() == null || item.getItem().getPrice() == null) return BigDecimal.ZERO;
        return item.getItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private List<OrderItemEvent> convertToOrderItemEvents(List<OrderItem> orderItems) {
        if (orderItems == null) return List.of();

        return orderItems.stream()
                .map(oi -> new OrderItemEvent(
                        oi.getItem() != null ? oi.getItem().getId() : null,
                        oi.getItem() != null ? oi.getItem().getName() : "Unknown Item",
                        oi.getItem() != null ? oi.getItem().getPrice() : BigDecimal.ZERO,
                        oi.getQuantity() != null ? oi.getQuantity() : 0))
                .toList();
    }
}
