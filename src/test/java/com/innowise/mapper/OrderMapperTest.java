package com.innowise.mapper;

import com.innowise.model.dto.OrderDto;
import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.dto.UserInfoDto;
import com.innowise.model.entity.Item;
import com.innowise.model.entity.Order;
import com.innowise.model.entity.OrderItem;
import com.innowise.model.enums.OrderStatus;
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
        order.setUserId("user-123");
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
                "user-123",
                OrderStatus.CONFIRMED,
                LocalDateTime.now(),
                List.of(itemDto),
                new UserInfoDto("user-1", "John", "Doe", "john@example.com"));

        Order order = orderMapper.toEntity(dto);

        assertNotNull(order);
        assertEquals(dto.id(), order.getId());
        assertEquals(dto.userId(), order.getUserId());
        assertEquals(dto.status(), order.getStatus());
        assertTrue(order.getItems() == null || order.getItems().isEmpty());
    }

    @Test
    void updateEntity_ShouldUpdateFieldsWithoutChangingId() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUserId("user-1");
        order.setCreatedDate(LocalDateTime.now());

        OrderDto dto = new OrderDto(
                999L,
                "user-2",
                OrderStatus.CONFIRMED,
                LocalDateTime.now(),
                List.of(),
                null);

        orderMapper.updateEntity(order, dto);

        assertEquals(1L, order.getId());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals("user-2", order.getUserId());
        assertEquals(dto.createdDate(), order.getCreatedDate());
    }
}
