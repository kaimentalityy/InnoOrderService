package com.innowise.event;

import com.innowise.model.enums.EventType;
import com.innowise.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCreatedEventTest {

    @Test
    void noArgsConstructor_shouldCreateEventWithDefaults() {
        OrderCreatedEvent event = new OrderCreatedEvent();

        assertThat(event).isNotNull();
        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventType()).isEqualTo(EventType.ORDER_CREATE);
        assertThat(event.getEventTimestamp()).isNotNull();
        assertThat(event.getOrderId()).isNull();
        assertThat(event.getUserId()).isNull();
        assertThat(event.getTotalAmount()).isNull();
        assertThat(event.getStatus()).isNull();
        assertThat(event.getItems()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateEventWithAllFields() {
        String eventId = "event-123";
        EventType eventType = EventType.ORDER_CREATE;
        LocalDateTime timestamp = LocalDateTime.now();
        Long orderId = 100L;
        Long userId = 200L;
        BigDecimal totalAmount = new BigDecimal("150.00");
        OrderStatus status = OrderStatus.PAYMENT_PENDING;
        List<OrderItemEvent> items = createTestItems();

        OrderCreatedEvent event = new OrderCreatedEvent(
                eventId, eventType, timestamp, orderId, userId, status, totalAmount, items);

        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getEventTimestamp()).isEqualTo(timestamp);
        assertThat(event.getOrderId()).isEqualTo(orderId);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getTotalAmount()).isEqualByComparingTo(totalAmount);
        assertThat(event.getStatus()).isEqualTo(status);
        assertThat(event.getItems()).hasSize(2);
    }

    @Test
    void builder_shouldCreateEventWithAllFields() {
        String eventId = "event-456";
        EventType eventType = EventType.ORDER_CREATE;
        LocalDateTime timestamp = LocalDateTime.now();
        Long orderId = 300L;
        Long userId = 400L;
        BigDecimal totalAmount = new BigDecimal("250.50");
        OrderStatus status = OrderStatus.CONFIRMED;
        List<OrderItemEvent> items = createTestItems();

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventId(eventId)
                .eventType(eventType)
                .eventTimestamp(timestamp)
                .orderId(orderId)
                .userId(userId)
                .totalAmount(totalAmount)
                .status(status)
                .items(items)
                .build();

        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getEventTimestamp()).isEqualTo(timestamp);
        assertThat(event.getOrderId()).isEqualTo(orderId);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getTotalAmount()).isEqualByComparingTo(totalAmount);
        assertThat(event.getStatus()).isEqualTo(status);
        assertThat(event.getItems()).isEqualTo(items);
    }

    @Test
    void builder_shouldCreateEventWithPartialFields() {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(500L)
                .userId(600L)
                .build();

        assertThat(event.getOrderId()).isEqualTo(500L);
        assertThat(event.getUserId()).isEqualTo(600L);
        assertThat(event.getEventId()).isNotNull();
    }

    private List<OrderItemEvent> createTestItems() {
        OrderItemEvent item1 = OrderItemEvent.builder()
                .itemId(1L)
                .itemName("Test Item 1")
                .price(new BigDecimal("50.00"))
                .quantity(2)
                .build();

        OrderItemEvent item2 = OrderItemEvent.builder()
                .itemId(2L)
                .itemName("Test Item 2")
                .price(new BigDecimal("75.00"))
                .quantity(1)
                .build();

        return Arrays.asList(item1, item2);
    }
}