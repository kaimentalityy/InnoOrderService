package com.innowise.service.impl;

import com.innowise.client.UserServiceClient;
import com.innowise.dao.repository.ItemRepository;
import com.innowise.dao.repository.OrderRepository;
import com.innowise.event.OrderCreatedEvent;
import com.innowise.mapper.OrderMapper;
import com.innowise.model.dto.OrderDto;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

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

    private Order order;
    private OrderDto orderDto;
    private UserInfoDto userInfoDto;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setUserId("user-10");
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setCreatedDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        orderDto = new OrderDto(
                1L,
                "user-10",
                OrderStatus.PAYMENT_PENDING,
                order.getCreatedDate(),
                List.of(),
                null);

        userInfoDto = new UserInfoDto("user-10", "John", "Doe", "test@example.com");
    }

    private void setupTimerMock() {
        when(orderProcessingTimer.record(any(java.util.function.Supplier.class))).thenAnswer(invocation -> {
            java.util.function.Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    void calculateTotalAmount_nullOrEmpty_returnsZero() throws Exception {
        var method = OrderServiceImpl.class.getDeclaredMethod("calculateTotalAmount", Order.class);
        method.setAccessible(true);
        Order emptyOrder = new Order();
        emptyOrder.setItems(null);
        BigDecimal result1 = (BigDecimal) method.invoke(orderService, emptyOrder);
        assertThat(result1).isEqualByComparingTo(BigDecimal.ZERO);

        emptyOrder.setItems(new ArrayList<>());
        BigDecimal result2 = (BigDecimal) method.invoke(orderService, emptyOrder);
        assertThat(result2).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculateTotalAmount_withItems_sumsCorrectly() throws Exception {
        Item item = new Item();
        item.setPrice(new BigDecimal("10.00"));
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(3);
        order.setItems(List.of(orderItem));

        var method = OrderServiceImpl.class.getDeclaredMethod("calculateTotalAmount", Order.class);
        method.setAccessible(true);
        BigDecimal total = (BigDecimal) method.invoke(orderService, order);
        assertThat(total).isEqualByComparingTo(new BigDecimal("30.00"));
    }

    @Test
    void calculateItemTotal_variousScenarios() throws Exception {
        var method = OrderServiceImpl.class.getDeclaredMethod("calculateItemTotal", OrderItem.class);
        method.setAccessible(true);

        OrderItem nullItem = new OrderItem();
        nullItem.setItem(null);
        nullItem.setQuantity(5);
        BigDecimal r1 = (BigDecimal) method.invoke(orderService, nullItem);
        assertThat(r1).isEqualByComparingTo(BigDecimal.ZERO);

        Item item = new Item();
        item.setPrice(null);
        OrderItem oi = new OrderItem();
        oi.setItem(item);
        oi.setQuantity(2);
        BigDecimal r2 = (BigDecimal) method.invoke(orderService, oi);
        assertThat(r2).isEqualByComparingTo(BigDecimal.ZERO);

        item.setPrice(new BigDecimal("7.50"));
        oi.setQuantity(4);
        BigDecimal r3 = (BigDecimal) method.invoke(orderService, oi);
        assertThat(r3).isEqualByComparingTo(new BigDecimal("30.00"));
    }

    @Test
    void convertToOrderItemEvents_nullList_returnsEmpty() throws Exception {
        var method = OrderServiceImpl.class.getDeclaredMethod("convertToOrderItemEvents", List.class);
        method.setAccessible(true);
        List<?> result = (List<?>) method.invoke(orderService, (Object) null);
        assertThat(result).isEmpty();
    }

    @Test
    void fetchUserInfo_usesEmailWhenProvided() throws Exception {
        var method = OrderServiceImpl.class.getDeclaredMethod("fetchUserInfo", String.class, String.class);
        method.setAccessible(true);
        when(userServiceClient.getUserByEmail(eq("mail@example.com"))).thenReturn(userInfoDto);
        UserInfoDto fetched = (UserInfoDto) method.invoke(orderService, "user-1", "mail@example.com");
        assertThat(fetched).isSameAs(userInfoDto);
        verify(userServiceClient, never()).getUserById(anyString());
    }

    @Test
    void fetchUserInfo_usesIdWhenEmailMissing() throws Exception {
        var method = OrderServiceImpl.class.getDeclaredMethod("fetchUserInfo", String.class, String.class);
        method.setAccessible(true);
        when(userServiceClient.getUserById(eq("user-10"))).thenReturn(userInfoDto);
        UserInfoDto fetched = (UserInfoDto) method.invoke(orderService, "user-10", null);
        assertThat(fetched).isSameAs(userInfoDto);
        verify(userServiceClient, never()).getUserByEmail(anyString());
    }

    @Test
    void updateOrderStatus_callsRepositoryAndMapper() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(eq("user-10"))).thenReturn(userInfoDto);

        OrderDto result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertThat(result).isNotNull();
        verify(orderRepository).save(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void create_sendsOrderCreatedEvent() {
        setupTimerMock();
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(eq("user-10"))).thenReturn(userInfoDto);

        OrderDto result = orderService.create(orderDto);
        assertThat(result).isNotNull();

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(orderEventProducer).sendOrderCreatedEvent(eventCaptor.capture());
        OrderCreatedEvent captured = eventCaptor.getValue();
        assertThat(captured.getOrderId()).isEqualTo(order.getId());
        assertThat(captured.getUserId()).isEqualTo(order.getUserId());
        assertThat(captured.getStatus()).isEqualTo(order.getStatus());
    }
}
