package com.innowise.service.impl;

import com.innowise.client.UserServiceClient;
import com.innowise.dao.repository.ItemRepository;
import com.innowise.dao.repository.OrderRepository;
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
import com.innowise.service.kafka.OrderEventProducer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplCoverageTest {

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

    private OrderServiceImpl orderService;

    private Order order;
    private UserInfoDto userInfoDto;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(
                orderRepository, orderMapper, itemRepository,
                userServiceClient, orderEventProducer,
                ordersCreatedCounter, ordersPendingCounter,
                ordersCompletedCounter, ordersFailedCounter,
                orderProcessingTimer);

        order = new Order();
        order.setId(1L);
        order.setUserId("user-1");
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setCreatedDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        userInfoDto = new UserInfoDto("user-1", "John", "Doe", "john@test.com");

        lenient().when(orderProcessingTimer.record(any(java.util.function.Supplier.class))).thenAnswer(inv -> {
            java.util.function.Supplier<?> supplier = inv.getArgument(0);
            return supplier.get();
        });
    }


    @Test
    void delete_existingOrder_shouldSucceed() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        orderService.delete(1L);
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingOrder_shouldThrow() {
        when(orderRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> orderService.delete(1L))
                .isInstanceOf(OrderNotFoundException.class);
    }


    @Test
    void findById_existing_returnsDto() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById("user-1")).thenReturn(userInfoDto);

        OrderDto result = orderService.findById(1L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_nonExisting_shouldThrow() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.findById(99L))
                .isInstanceOf(OrderNotFoundException.class);
    }


    @Test
    void update_nonExistingOrder_shouldThrow() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        OrderDto dto = new OrderDto(99L, "user-1", OrderStatus.CONFIRMED, null, null, null);
        assertThatThrownBy(() -> orderService.update(99L, dto))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void update_allFields_shouldUpdateEverything() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item(1L, "Widget", new BigDecimal("10.00"));
        OrderItemDto itemDto = new OrderItemDto(null, null, 1L, 5);
        OrderDto updateDto = new OrderDto(1L, "user-2", OrderStatus.CONFIRMED, now,
                List.of(itemDto), null);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(any())).thenReturn(userInfoDto);

        OrderDto result = orderService.update(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(order.getUserId()).isEqualTo("user-2");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getCreatedDate()).isEqualTo(now);
    }

    @Test
    void update_withItems_existingItemsNull_shouldCreateNewList() {
        order.setItems(null);
        OrderItemDto itemDto = new OrderItemDto(null, null, 1L, 2);
        OrderDto updateDto = new OrderDto(1L, null, null, null, List.of(itemDto), null);

        Item item = new Item(1L, "Ball", new BigDecimal("5.00"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(any())).thenReturn(userInfoDto);

        OrderDto result = orderService.update(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(order.getItems()).hasSize(1);
    }

    @Test
    void update_withItems_usesItemIdFallbackToId() {
        OrderItemDto itemDto = new OrderItemDto(5L, null, null, null);
        OrderDto updateDto = new OrderDto(1L, null, null, null, List.of(itemDto), null);

        Item item = new Item(5L, "Gadget", new BigDecimal("15.00"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(any())).thenReturn(userInfoDto);

        OrderDto result = orderService.update(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    void update_withItems_itemNotFound_shouldThrow() {
        OrderItemDto itemDto = new OrderItemDto(null, null, 999L, 1);
        OrderDto updateDto = new OrderDto(1L, null, null, null, List.of(itemDto), null);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.update(1L, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Item not found");
    }


    @Test
    void create_exceptionDuringProcessing_incrementsFailedCounter() {
        OrderDto createDto = new OrderDto(null, "user-1", OrderStatus.PAYMENT_PENDING, null,
                List.of(new OrderItemDto(null, null, 999L, 1)), null);

        when(orderMapper.toEntity(any())).thenReturn(order);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(createDto))
                .isInstanceOf(RuntimeException.class);
        verify(ordersFailedCounter).increment();
    }

    @Test
    void create_withNullItems_shouldCreateOrderWithoutItems() {
        OrderDto createDto = new OrderDto(null, "user-1", OrderStatus.PAYMENT_PENDING, null, null, null);

        when(orderMapper.toEntity(any())).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById("user-1")).thenReturn(userInfoDto);

        OrderDto result = orderService.create(createDto);

        assertThat(result).isNotNull();
        verify(ordersCreatedCounter).increment();
        verify(ordersPendingCounter).increment();
    }


    @Test
    void searchOrders_withAllParameters() {
        LocalDateTime after = LocalDateTime.now().minusDays(1);
        LocalDateTime before = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserByEmail("test@test.com")).thenReturn(userInfoDto);

        Page<OrderDto> result = orderService.searchOrders("user-1", "test@test.com",
                "PAYMENT_PENDING", after, before, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchOrders_withNoParameters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById("user-1")).thenReturn(userInfoDto);

        Page<OrderDto> result = orderService.searchOrders(null, null, null, null, null, pageable);

        assertThat(result).isNotNull();
    }


    @Test
    void updateOrderStatus_toCancelled_incrementsFailedCounter() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(any())).thenReturn(userInfoDto);

        orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        verify(ordersFailedCounter).increment();
        verify(ordersCompletedCounter, never()).increment();
    }

    @Test
    void updateOrderStatus_toPending_doesNotIncrementCounters() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);
        when(orderMapper.orderItemsToDtos(any())).thenReturn(List.of());
        when(userServiceClient.getUserById(any())).thenReturn(userInfoDto);

        orderService.updateOrderStatus(1L, OrderStatus.PAYMENT_PENDING);

        verify(ordersCompletedCounter, never()).increment();
        verify(ordersFailedCounter, never()).increment();
    }

    @Test
    void updateOrderStatus_notFound_shouldThrow() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus(99L, OrderStatus.CONFIRMED))
                .isInstanceOf(OrderNotFoundException.class);
    }


    @Test
    void convertToOrderItemEvents_withNullItemFields() throws Exception {
        var method = OrderServiceImpl.class.getDeclaredMethod("convertToOrderItemEvents", List.class);
        method.setAccessible(true);

        OrderItem oi1 = new OrderItem();
        oi1.setItem(null);
        oi1.setQuantity(3);

        OrderItem oi2 = new OrderItem();
        Item itemNoName = new Item();
        itemNoName.setId(null);
        itemNoName.setName(null);
        itemNoName.setPrice(null);
        oi2.setItem(itemNoName);
        oi2.setQuantity(null);

        @SuppressWarnings("unchecked")
        List<OrderItemEvent> events = (List<OrderItemEvent>) method.invoke(orderService, List.of(oi1, oi2));

        assertThat(events).hasSize(2);

        assertThat(events.get(0).getItemId()).isNull();
        assertThat(events.get(0).getItemName()).isEqualTo("Unknown Item");
        assertThat(events.get(0).getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(events.get(0).getQuantity()).isEqualTo(3);

        assertThat(events.get(1).getQuantity()).isEqualTo(0);
    }


    @Test
    void fetchUserInfo_emptyEmail_usesId() throws Exception {
        var method = OrderServiceImpl.class.getDeclaredMethod("fetchUserInfo", String.class, String.class);
        method.setAccessible(true);

        when(userServiceClient.getUserById("user-1")).thenReturn(userInfoDto);
        UserInfoDto result = (UserInfoDto) method.invoke(orderService, "user-1", "");

        assertThat(result).isSameAs(userInfoDto);
        verify(userServiceClient, never()).getUserByEmail(any());
    }
}
