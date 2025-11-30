package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.OrderDto;
import com.innowise.orderservice.model.dto.OrderItemDto;
import com.innowise.orderservice.model.dto.UserInfoDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = Mappers.getMapper(OrderMapper.class);
    }

    @Test
    void toDto_ShouldMapEntityToDto() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(123L);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setCreatedDate(LocalDateTime.now());

        Item itemEntity = new Item();
        itemEntity.setId(10L);

        OrderItem item = new OrderItem();
        item.setId(100L);
        item.setQuantity(2);
        item.setOrder(order);
        item.setItem(itemEntity);

        order.setItems(List.of(item));

        OrderDto dto = orderMapper.toDto(order);

        assertNotNull(dto);
        assertEquals(order.getId(), dto.id());
        assertEquals(order.getUserId(), dto.userId());
        assertEquals(order.getStatus(), dto.status());
        assertEquals(1, dto.items().size());
        assertNull(dto.userInfo());

        OrderItemDto dtoItem = dto.items().get(0);
        assertEquals(item.getId(), dtoItem.id());
        assertEquals(item.getQuantity(), dtoItem.quantity());
        assertEquals(order.getId(), dtoItem.orderId());
        assertEquals(itemEntity.getId(), dtoItem.itemId());
    }

    @Test
    void toEntity_ShouldMapDtoToEntity() {
        OrderItemDto itemDto = new OrderItemDto(200L, 1L, 10L, 3);

        OrderDto dto = new OrderDto(
                1L,
                123L,
                OrderStatus.CONFIRMED,
                LocalDateTime.now(),
                List.of(itemDto),
                new UserInfoDto(1L, "John", "Doe", "john@example.com")
        );

        Order order = orderMapper.toEntity(dto);

        assertNotNull(order);
        assertEquals(dto.id(), order.getId());
        assertEquals(dto.userId(), order.getUserId());
        assertEquals(dto.status(), order.getStatus());
        assertEquals(0, order.getItems().size());
    }

    @Test
    void updateEntity_ShouldUpdateFieldsWithoutChangingId() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUserId(1L);
        order.setCreatedDate(LocalDateTime.now());

        OrderDto dto = new OrderDto(
                999L,
                2L,
                OrderStatus.CONFIRMED,
                LocalDateTime.now(),
                List.of(),
                null
        );

        orderMapper.updateEntity(order, dto);

        assertEquals(1L, order.getId());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(2L, order.getUserId());
        assertEquals(dto.createdDate(), order.getCreatedDate());
    }
}
