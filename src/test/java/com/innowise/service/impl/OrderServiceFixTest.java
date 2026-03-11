package com.innowise.service.impl;

import com.innowise.client.UserServiceClient;
import com.innowise.dao.repository.ItemRepository;
import com.innowise.dao.repository.OrderRepository;
import com.innowise.mapper.OrderMapper;
import com.innowise.model.dto.OrderDto;
import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.dto.UserInfoDto;
import com.innowise.model.entity.Item;
import com.innowise.model.entity.Order;
import com.innowise.model.entity.OrderItem;
import com.innowise.model.enums.OrderStatus;
import com.innowise.service.kafka.OrderEventProducer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceFixTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private OrderEventProducer orderEventProducer;

    @Mock
    private Counter ordersCreatedCounter;

    @Mock
    private Counter ordersPendingCounter;

    @Mock
    private Counter ordersCompletedCounter;

    @Mock
    private Counter ordersFailedCounter;

    @Mock
    private Timer orderProcessingTimer;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private Item testItem;
    private UserInfoDto userInfoDto;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId("user-6");
        testOrder.setStatus(OrderStatus.PAYMENT_PENDING);
        testOrder.setCreatedDate(LocalDateTime.now());

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("ball");
        testItem.setPrice(new BigDecimal("22.00"));

        userInfoDto = new UserInfoDto("user-6", "John", "Doe", "john@example.com");

        lenient().when(orderProcessingTimer.record(any(java.util.function.Supplier.class))).thenAnswer(invocation -> {
            java.util.function.Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    void create_withItemLikePayload_shouldWork() {
        OrderItemDto itemLikeDto = new OrderItemDto(
                1L,
                null,
                null,
                null
        );

        OrderDto createDto = new OrderDto(
                null,
                "user-6",
                OrderStatus.PAYMENT_PENDING,
                null,
                List.of(itemLikeDto),
                null);

        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(testOrder);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(eq("user-6"))).thenReturn(userInfoDto);

        OrderDto result = orderService.create(createDto);

        assertThat(result).isNotNull();
        verify(itemRepository).findById(1L);
        verify(orderRepository).save(testOrder);

        assertThat(testOrder.getItems()).hasSize(1);
        OrderItem savedOrderItem = testOrder.getItems().get(0);
        assertThat(savedOrderItem.getItem().getId()).isEqualTo(1L);
        assertThat(savedOrderItem.getQuantity()).isEqualTo(1);
        assertThat(savedOrderItem.getOrder()).isEqualTo(testOrder);
    }

    @Test
    void create_withProperOrderItemDto_shouldWork() {
        OrderItemDto properDto = new OrderItemDto(
                null,
                null,
                1L,
                3
        );

        OrderDto createDto = new OrderDto(
                null,
                "user-6",
                OrderStatus.PAYMENT_PENDING,
                null,
                List.of(properDto),
                null);

        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(testOrder);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(eq("user-6"))).thenReturn(userInfoDto);

        OrderDto result = orderService.create(createDto);

        assertThat(result).isNotNull();
        verify(itemRepository).findById(1L);
        verify(orderRepository).save(testOrder);

        assertThat(testOrder.getItems()).hasSize(1);
        OrderItem savedOrderItem = testOrder.getItems().get(0);
        assertThat(savedOrderItem.getItem().getId()).isEqualTo(1L);
        assertThat(savedOrderItem.getQuantity()).isEqualTo(3);
        assertThat(savedOrderItem.getOrder()).isEqualTo(testOrder);
    }

    @Test
    void update_withItems_shouldWork() {
        OrderItemDto itemDto = new OrderItemDto(
                1L,
                null,
                null,
                2
        );

        OrderDto updateDto = new OrderDto(
                1L,
                null,
                null,
                null,
                List.of(itemDto),
                null);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(eq("user-6"))).thenReturn(userInfoDto);

        OrderDto result = orderService.update(1L, updateDto);

        assertThat(result).isNotNull();
        verify(itemRepository).findById(1L);
        verify(orderRepository).save(testOrder);

        assertThat(testOrder.getItems()).hasSize(1);
        OrderItem savedOrderItem = testOrder.getItems().get(0);
        assertThat(savedOrderItem.getItem().getId()).isEqualTo(1L);
        assertThat(savedOrderItem.getQuantity()).isEqualTo(2);
        assertThat(savedOrderItem.getOrder()).isEqualTo(testOrder);

        assertThat(testOrder.getUserId()).isEqualTo("user-6");
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
        assertThat(testOrder.getCreatedDate()).isNotNull();
    }

    @Test
    void update_partialFieldUpdate_shouldPreserveExistingValues() {
        OrderDto updateDto = new OrderDto(
                1L,
                null,
                OrderStatus.CONFIRMED,
                null,
                null,
                null);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(eq("user-6"))).thenReturn(userInfoDto);

        OrderDto result = orderService.update(1L, updateDto);

        assertThat(result).isNotNull();
        verify(orderRepository).save(testOrder);

        assertThat(testOrder.getUserId()).isEqualTo("user-6");
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(testOrder.getCreatedDate()).isNotNull();
    }
}
