package com.innowise.mapper;

import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.entity.Item;
import com.innowise.model.entity.Order;
import com.innowise.model.entity.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemMapperTest {

    private OrderItemMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(OrderItemMapper.class);
    }

    @Test
    void toDto_ShouldMapEntityToDto() {
        Order order = new Order();
        order.setId(1L);

        Item item = new Item();
        item.setId(10L);

        OrderItem entity = new OrderItem();
        entity.setId(100L);
        entity.setQuantity(5);
        entity.setOrder(order);
        entity.setItem(item);

        OrderItemDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.id());
        assertEquals(entity.getQuantity(), dto.quantity());
        assertEquals(order.getId(), dto.orderId());
        assertEquals(item.getId(), dto.itemId());
    }

    @Test
    void toEntity_ShouldMapDtoToEntity() {
        OrderItemDto dto = new OrderItemDto(200L, 1L, 10L, 3);

        OrderItem entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.quantity(), entity.getQuantity());

        assertNotNull(entity.getItem());
        assertEquals(dto.itemId(), entity.getItem().getId());

        assertNull(entity.getOrder());
    }

    @Test
    void updateEntity_ShouldUpdateFieldsWithoutChangingId() {
        OrderItem entity = new OrderItem();
        entity.setId(1L);
        entity.setQuantity(2);

        OrderItemDto dto = new OrderItemDto(2L, 5L, 6L, 10);

        mapper.updateEntity(entity, dto);

        assertEquals(1L, entity.getId());
        assertEquals(dto.quantity(), entity.getQuantity());
        assertNull(entity.getOrder());
        assertNull(entity.getItem());
    }
}
