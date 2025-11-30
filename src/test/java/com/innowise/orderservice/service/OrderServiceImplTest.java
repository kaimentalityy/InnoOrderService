package com.innowise.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.dao.repository.OrderRepository;

import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.dto.OrderDto;
import com.innowise.orderservice.model.dto.UserInfoDto;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.enums.OrderStatus;
import com.innowise.orderservice.service.impl.OrderServiceImpl;
import com.innowise.orderservice.service.kafka.OrderEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order entity;
    private OrderDto dto;
    private UserInfoDto userInfo;

    @BeforeEach
    void setUp() throws Exception {
        entity = new Order();
        entity.setId(1L);
        entity.setStatus(OrderStatus.CONFIRMED);
        entity.setUserId(123L);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setItems(new ArrayList<>());

        userInfo = new UserInfoDto(123L, "John", "Doe", "john@example.com");

        dto = new OrderDto(
                1L,
                123L,
                OrderStatus.CONFIRMED,
                entity.getCreatedDate(),
                List.of(),
                userInfo);
    }

    @Test
    @DisplayName("Should create order and save to outbox")
    void create_ShouldReturnDtoWithUserInfo() throws JsonProcessingException {
        
        when(orderMapper.toEntity(dto)).thenReturn(entity);
        when(orderRepository.save(entity)).thenReturn(entity);
        when(orderMapper.orderItemsToDtos(anyList())).thenReturn(Collections.emptyList());
        when(userServiceClient.getUserById(entity.getUserId())).thenReturn(userInfo);

        
        OrderDto result = orderService.create(dto);

        
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo(123L);
        assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(result.userInfo()).isEqualTo(userInfo);

        verify(orderRepository).save(entity);

    }

    @Test
    @DisplayName("Should update order successfully")
    void update_ShouldReturnUpdatedDto() {
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderRepository.save(entity)).thenReturn(entity);
        when(orderMapper.orderItemsToDtos(anyList())).thenReturn(Collections.emptyList());
        when(userServiceClient.getUserById(entity.getUserId())).thenReturn(userInfo);

        
        OrderDto result = orderService.update(1L, dto);

        
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.userInfo()).isEqualTo(userInfo);

        verify(orderMapper).updateEntity(entity, dto);
        verify(orderRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent order")
    void update_ShouldThrowIfNotFound() {
        
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> orderService.update(1L, dto))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should delete order successfully")
    void delete_ShouldCallRepository() {
        
        when(orderRepository.existsById(1L)).thenReturn(true);

        
        orderService.delete(1L);

        
        verify(orderRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent order")
    void delete_ShouldThrowIfNotFound() {
        
        when(orderRepository.existsById(1L)).thenReturn(false);

        
        assertThatThrownBy(() -> orderService.delete(1L))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should find order by ID with user info")
    void findById_ShouldReturnDtoWithUserInfo() {
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderMapper.orderItemsToDtos(anyList())).thenReturn(Collections.emptyList());
        when(userServiceClient.getUserById(entity.getUserId())).thenReturn(userInfo);

        
        OrderDto result = orderService.findById(1L);

        
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.userInfo()).isEqualTo(userInfo);
    }

    @Test
    @DisplayName("Should throw exception when finding non-existent order")
    void findById_ShouldThrowIfNotFound() {
        
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> orderService.findById(1L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("Should search orders with user ID filter")
    void searchOrders_ShouldReturnDtosWithUserInfo() {
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(entity));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(orderMapper.orderItemsToDtos(anyList())).thenReturn(Collections.emptyList());
        when(userServiceClient.getUserById(entity.getUserId())).thenReturn(userInfo);

        
        Page<OrderDto> result = orderService.searchOrders(123L, null, "CONFIRMED", null, null, pageable);

        
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).userInfo()).isEqualTo(userInfo);
    }

    @Test
    @DisplayName("Should search orders using email")
    void searchOrders_ShouldUseEmailIfProvided() {
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(entity));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(orderMapper.orderItemsToDtos(anyList())).thenReturn(Collections.emptyList());
        when(userServiceClient.getUserByEmail("john@example.com")).thenReturn(userInfo);

        
        Page<OrderDto> result = orderService.searchOrders(null, "john@example.com", "CONFIRMED", null, null, pageable);

        
        assertThat(result.getContent().get(0).userInfo()).isEqualTo(userInfo);
        verify(userServiceClient).getUserByEmail("john@example.com");
        verify(userServiceClient, never()).getUserById(anyLong());
    }

    @Test
    @DisplayName("Should search orders with date range filters")
    void searchOrders_ShouldFilterByDateRange() {
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(entity));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(orderMapper.orderItemsToDtos(anyList())).thenReturn(Collections.emptyList());
        when(userServiceClient.getUserById(entity.getUserId())).thenReturn(userInfo);

        
        Page<OrderDto> result = orderService.searchOrders(null, null, null, startDate, endDate, pageable);

        
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should search orders with all filters")
    void searchOrders_ShouldApplyAllFilters() {
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(entity));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(orderMapper.orderItemsToDtos(anyList())).thenReturn(Collections.emptyList());
        when(userServiceClient.getUserById(123L)).thenReturn(userInfo);

        
        Page<OrderDto> result = orderService.searchOrders(123L, null, "CONFIRMED", startDate, endDate, pageable);

        
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).status()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should return empty page when no orders match filters")
    void searchOrders_ShouldReturnEmptyPage() {
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> emptyPage = new PageImpl<>(Collections.emptyList());

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        
        Page<OrderDto> result = orderService.searchOrders(999L, null, "CONFIRMED", null, null, pageable);

        
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Should update order status successfully")
    void updateOrderStatus_ShouldUpdateStatus() {
        
        OrderStatus newStatus = OrderStatus.CONFIRMED;
        when(orderRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderRepository.save(entity)).thenReturn(entity);
        when(orderMapper.toDto(entity)).thenReturn(dto);

        
        OrderDto result = orderService.updateOrderStatus(1L, newStatus);

        
        assertThat(result).isNotNull();
        verify(orderRepository).save(entity);
        assertThat(entity.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("Should throw exception when updating status of non-existent order")
    void updateOrderStatus_ShouldThrowIfNotFound() {
        
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository, never()).save(any(Order.class));
    }

    private Order createOrderWithItems() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(123L);
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setCreatedDate(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setQuantity(2);

        
        com.innowise.orderservice.model.entity.Item itemEntity = new com.innowise.orderservice.model.entity.Item();
        itemEntity.setId(1L);
        itemEntity.setName("Item 1");
        itemEntity.setPrice(new BigDecimal("50.00"));

        item.setItem(itemEntity);
        items.add(item);

        order.setItems(items);
        return order;
    }
}