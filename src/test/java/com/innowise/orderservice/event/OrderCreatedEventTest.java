package com.innowise.orderservice.event;

import com.innowise.orderservice.model.enums.EventType;
import com.innowise.orderservice.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCreatedEventTest {

    @Test
    void defaultConstructor_shouldInitializeEventIdAndTimestamp() {
        OrderCreatedEvent event = new OrderCreatedEvent();

        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventTimestamp()).isNotNull();
        assertThat(event.getEventType()).isEqualTo(EventType.ORDER_CREATE);

        // Other fields should be null by default
        assertThat(event.getOrderId()).isNull();
        assertThat(event.getUserId()).isNull();
        assertThat(event.getStatus()).isNull();
        assertThat(event.getTotalAmount()).isNull();
        assertThat(event.getItems()).isNull();
    }

    @Test
    void fullConstructor_shouldSetAllFieldsCorrectly() {
        Long orderId = 1L;
        Long userId = 2L;
        OrderStatus status = OrderStatus.CONFIRMED;
        BigDecimal totalAmount = new BigDecimal("123.45");
        List<OrderItemEvent> items = List.of(new OrderItemEvent(), new OrderItemEvent());

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, status, totalAmount, items);

        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventTimestamp()).isNotNull();
        assertThat(event.getEventType()).isEqualTo(EventType.ORDER_CREATE);

        assertThat(event.getOrderId()).isEqualTo(orderId);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getStatus()).isEqualTo(status);
        assertThat(event.getTotalAmount()).isEqualByComparingTo(totalAmount);
        assertThat(event.getItems()).isEqualTo(items);
    }

    @Test
    void setters_shouldUpdateAllFields() {
        OrderCreatedEvent event = new OrderCreatedEvent();

        Long orderId = 10L;
        Long userId = 20L;
        OrderStatus status = OrderStatus.CONFIRMED;
        BigDecimal totalAmount = new BigDecimal("999.99");
        List<OrderItemEvent> items = List.of(new OrderItemEvent());

        event.setOrderId(orderId);
        event.setUserId(userId);
        event.setStatus(status);
        event.setTotalAmount(totalAmount);
        event.setItems(items);

        assertThat(event.getOrderId()).isEqualTo(orderId);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getStatus()).isEqualTo(status);
        assertThat(event.getTotalAmount()).isEqualByComparingTo(totalAmount);
        assertThat(event.getItems()).isEqualTo(items);

        // eventId and eventTimestamp should still be initialized
        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventTimestamp()).isNotNull();
    }
}
