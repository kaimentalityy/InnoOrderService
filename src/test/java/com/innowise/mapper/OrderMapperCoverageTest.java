package com.innowise.mapper;

import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.entity.Item;
import com.innowise.model.entity.Order;
import com.innowise.model.entity.OrderItem;
import com.innowise.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperCoverageTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = Mappers.getMapper(OrderMapper.class);
    }

    @Test
    void orderItemsToDtos_nullList_shouldReturnEmptyList() {
        List<OrderItemDto> result = orderMapper.orderItemsToDtos(null);
        assertThat(result).isEmpty();
    }

    @Test
    void orderItemsToDtos_emptyList_shouldReturnEmptyList() {
        List<OrderItemDto> result = orderMapper.orderItemsToDtos(List.of());
        assertThat(result).isEmpty();
    }

    @Test
    void orderItemsToDtos_withNullEntries_shouldFilterThem() {
        List<OrderItem> items = Arrays.asList(null, null);
        List<OrderItemDto> result = orderMapper.orderItemsToDtos(items);
        assertThat(result).isEmpty();
    }

    @Test
    void orderItemsToDtos_withNullItem_shouldFilterIt() {
        OrderItem oi = new OrderItem();
        oi.setId(1L);
        oi.setItem(null); // null item
        Order order = new Order();
        order.setId(1L);
        oi.setOrder(order);
        oi.setQuantity(2);

        List<OrderItemDto> result = orderMapper.orderItemsToDtos(List.of(oi));
        assertThat(result).isEmpty();
    }

    @Test
    void orderItemsToDtos_withNullOrder_shouldFilterIt() {
        OrderItem oi = new OrderItem();
        oi.setId(1L);
        Item item = new Item(1L, "Widget", BigDecimal.TEN);
        oi.setItem(item);
        oi.setOrder(null); // null order
        oi.setQuantity(2);

        List<OrderItemDto> result = orderMapper.orderItemsToDtos(List.of(oi));
        assertThat(result).isEmpty();
    }

    @Test
    void orderItemsToDtos_validItems_shouldMapCorrectly() {
        Order order = new Order();
        order.setId(10L);
        order.setUserId("user-1");
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setCreatedDate(LocalDateTime.now());

        Item item = new Item(5L, "Gadget", new BigDecimal("49.99"));

        OrderItem oi = new OrderItem();
        oi.setId(1L);
        oi.setOrder(order);
        oi.setItem(item);
        oi.setQuantity(3);

        List<OrderItemDto> result = orderMapper.orderItemsToDtos(List.of(oi));

        assertThat(result).hasSize(1);
        OrderItemDto dto = result.get(0);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.orderId()).isEqualTo(10L);
        assertThat(dto.itemId()).isEqualTo(5L);
        assertThat(dto.quantity()).isEqualTo(3);
    }

    @Test
    void orderItemsToDtos_mixedValidAndInvalid_shouldFilterInvalid() {
        Order order = new Order();
        order.setId(1L);
        Item item = new Item(1L, "A", BigDecimal.ONE);

        OrderItem valid = new OrderItem();
        valid.setId(1L);
        valid.setOrder(order);
        valid.setItem(item);
        valid.setQuantity(1);

        OrderItem invalidNoItem = new OrderItem();
        invalidNoItem.setId(2L);
        invalidNoItem.setOrder(order);
        invalidNoItem.setItem(null);
        invalidNoItem.setQuantity(1);

        List<OrderItemDto> result = orderMapper.orderItemsToDtos(Arrays.asList(valid, invalidNoItem, null));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void toDto_null_shouldReturnNull() {
        assertThat(orderMapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_null_shouldReturnNull() {
        assertThat(orderMapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_null_shouldNotThrow() {
        Order order = new Order();
        orderMapper.updateEntity(order, null);
    }
}
