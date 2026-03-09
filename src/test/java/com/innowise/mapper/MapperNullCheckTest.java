package com.innowise.mapper;

import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.entity.Item;
import com.innowise.model.entity.Order;
import com.innowise.model.entity.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class MapperNullCheckTest {

    private ItemMapper itemMapper;
    private OrderItemMapper orderItemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = Mappers.getMapper(ItemMapper.class);
        orderItemMapper = Mappers.getMapper(OrderItemMapper.class);
    }

    @Test
    void itemMapper_nullChecks() {
        assertThat(itemMapper.toDto(null)).isNull();
        assertThat(itemMapper.toEntity(null)).isNull();

        Item item = new Item();
        itemMapper.updateEntity(item, null);
    }

    @Test
    void orderItemMapper_nullChecks() {
        assertThat(orderItemMapper.toDto(null)).isNull();
        assertThat(orderItemMapper.toEntity(null)).isNull();

        OrderItem orderItem = new OrderItem();
        orderItemMapper.updateEntity(orderItem, null);

        OrderItem entry = new OrderItem();
        OrderItemDto dto = orderItemMapper.toDto(entry);
        assertThat(dto.itemId()).isNull();
        assertThat(dto.orderId()).isNull();

        entry.setItem(null);
        entry.setOrder(null);
        assertThat(orderItemMapper.toDto(entry).itemId()).isNull();

        entry.setItem(new Item());
        entry.setOrder(new Order());
        assertThat(orderItemMapper.toDto(entry).itemId()).isNull();
    }
}
