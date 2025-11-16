package com.innowise.orderservice.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemEventTest {

    @Test
    void noArgsConstructor_shouldInitializeObject() {
        OrderItemEvent item = new OrderItemEvent();

        assertThat(item.getItemId()).isNull();
        assertThat(item.getItemName()).isNull();
        assertThat(item.getPrice()).isNull();
        assertThat(item.getQuantity()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        Long itemId = 1L;
        String itemName = "Test Item";
        BigDecimal price = new BigDecimal("9.99");
        Integer quantity = 5;

        OrderItemEvent item = new OrderItemEvent(itemId, itemName, price, quantity);

        assertThat(item.getItemId()).isEqualTo(itemId);
        assertThat(item.getItemName()).isEqualTo(itemName);
        assertThat(item.getPrice()).isEqualByComparingTo(price);
        assertThat(item.getQuantity()).isEqualTo(quantity);
    }

    @Test
    void setters_shouldUpdateAllFields() {
        OrderItemEvent item = new OrderItemEvent();

        Long itemId = 2L;
        String itemName = "Updated Item";
        BigDecimal price = new BigDecimal("19.99");
        Integer quantity = 10;

        item.setItemId(itemId);
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        assertThat(item.getItemId()).isEqualTo(itemId);
        assertThat(item.getItemName()).isEqualTo(itemName);
        assertThat(item.getPrice()).isEqualByComparingTo(price);
        assertThat(item.getQuantity()).isEqualTo(quantity);
    }
}
