package com.innowise.model.entity;

import com.innowise.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class EntityBoilerplateTest {

    @Test
    void itemBoilerplate() {
        Item item1 = new Item(1L, "Test", BigDecimal.TEN);
        Item item2 = new Item(1L, "Test", BigDecimal.TEN);
        Item item3 = new Item(2L, "Other", BigDecimal.ONE);

        assertThat(item1.getId()).isEqualTo(1L);
        assertThat(item1.getName()).isEqualTo("Test");
        assertThat(item1.getPrice()).isEqualTo(BigDecimal.TEN);

        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
        assertThat(item1).isNotEqualTo(item3);
        assertThat(item1.toString()).contains("Item");

        Item empty = new Item();
        empty.setId(5L);
        assertThat(empty.getId()).isEqualTo(5L);
    }

    @Test
    void orderBoilerplate() {
        LocalDateTime now = LocalDateTime.now();
        Order order1 = new Order();
        order1.setId(1L);
        order1.setUserId("user1");
        order1.setStatus(OrderStatus.CONFIRMED);
        order1.setCreatedDate(now);
        order1.setItems(new ArrayList<>());

        Order order2 = new Order();
        order2.setId(1L);
        order2.setUserId("user1");
        order2.setStatus(OrderStatus.CONFIRMED);
        order2.setCreatedDate(now);
        order2.setItems(new ArrayList<>());

        assertThat(order1.getId()).isEqualTo(1L);
        assertThat(order1.getUserId()).isEqualTo("user1");
        assertThat(order1.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order1.getCreatedDate()).isEqualTo(now);
        assertThat(order1.getItems()).isEmpty();

        assertThat(order1).isEqualTo(order2);
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
        assertThat(order1.toString()).contains("Order");

        Order empty = new Order();
        empty.setStatus(OrderStatus.CANCELLED);
        assertThat(empty.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void orderItemBoilerplate() {
        Order order = new Order();
        Item item = new Item();
        OrderItem oi1 = new OrderItem(1L, order, item, 5);
        OrderItem oi2 = new OrderItem(1L, order, item, 5);

        assertThat(oi1.getId()).isEqualTo(1L);
        assertThat(oi1.getOrder()).isEqualTo(order);
        assertThat(oi1.getItem()).isEqualTo(item);
        assertThat(oi1.getQuantity()).isEqualTo(5);

        assertThat(oi1).isEqualTo(oi2);
        assertThat(oi1.hashCode()).isEqualTo(oi2.hashCode());
        assertThat(oi1.toString()).contains("OrderItem");

        OrderItem empty = new OrderItem();
        empty.setQuantity(10);
        assertThat(empty.getQuantity()).isEqualTo(10);
    }
}
