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
                eventId, eventType, timestamp, orderId, userId, status, totalAmount, items
        );

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
        assertThat(event.getEventId()).isNotNull(); // Default value from builder
        assertThat(event.getEventType()).isEqualTo(EventType.ORDER_CREATE); // Default value
        assertThat(event.getEventTimestamp()).isNotNull(); // Default value
        assertThat(event.getTotalAmount()).isNull();
        assertThat(event.getStatus()).isNull();
        assertThat(event.getItems()).isNull();
    }

    @Test
    void setters_shouldUpdateAllFields() {
        OrderCreatedEvent event = new OrderCreatedEvent();
        String eventId = "event-789";
        EventType eventType = EventType.ORDER_CREATE;
        LocalDateTime timestamp = LocalDateTime.now();
        Long orderId = 700L;
        Long userId = 800L;
        BigDecimal totalAmount = new BigDecimal("999.99");
        OrderStatus status = OrderStatus.CONFIRMED;
        List<OrderItemEvent> items = createTestItems();

        event.setEventId(eventId);
        event.setEventType(eventType);
        event.setEventTimestamp(timestamp);
        event.setOrderId(orderId);
        event.setUserId(userId);
        event.setTotalAmount(totalAmount);
        event.setStatus(status);
        event.setItems(items);

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
    void getters_shouldReturnCorrectValues() {
        String eventId = "event-get";
        EventType eventType = EventType.ORDER_CREATE;
        LocalDateTime timestamp = LocalDateTime.now();
        Long orderId = 111L;
        Long userId = 222L;
        BigDecimal totalAmount = new BigDecimal("333.33");
        OrderStatus status = OrderStatus.CONFIRMED;
        List<OrderItemEvent> items = createTestItems();

        OrderCreatedEvent event = new OrderCreatedEvent(
                eventId, eventType, timestamp, orderId, userId, status, totalAmount, items
        );

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
    void equals_shouldReturnTrueForSameValues() {
        String eventId = "event-eq";
        Long orderId = 100L;
        Long userId = 200L;

        OrderCreatedEvent event1 = OrderCreatedEvent.builder()
                .eventId(eventId)
                .orderId(orderId)
                .userId(userId)
                .build();

        OrderCreatedEvent event2 = OrderCreatedEvent.builder()
                .eventId(eventId)
                .orderId(orderId)
                .userId(userId)
                .build();

        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentValues() {
        OrderCreatedEvent event1 = OrderCreatedEvent.builder()
                .eventId("event-1")
                .orderId(100L)
                .build();

        OrderCreatedEvent event2 = OrderCreatedEvent.builder()
                .eventId("event-2")
                .orderId(200L)
                .build();

        assertThat(event1).isNotEqualTo(event2);
        assertThat(event1.hashCode()).isNotEqualTo(event2.hashCode());
    }

    @Test
    void toString_shouldContainAllFields() {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventId("event-str")
                .eventType(EventType.ORDER_CREATE)
                .orderId(100L)
                .userId(200L)
                .totalAmount(new BigDecimal("150.00"))
                .status(OrderStatus.PAYMENT_PENDING)
                .build();

        String result = event.toString();

        assertThat(result).contains("event-str");
        assertThat(result).contains("ORDER_CREATE");
        assertThat(result).contains("100");
        assertThat(result).contains("200");
        assertThat(result).contains("150.00");
        assertThat(result).contains(OrderStatus.PAYMENT_PENDING.toString());
    }

    @Test
    void builder_shouldHandleEmptyItemsList() {
        List<OrderItemEvent> emptyItems = new ArrayList<>();

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(100L)
                .items(emptyItems)
                .build();

        assertThat(event.getItems()).isEmpty();
    }

    @Test
    void builder_shouldHandleNullItems() {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(100L)
                .items(null)
                .build();

        assertThat(event.getItems()).isNull();
    }

    @Test
    void builder_shouldHandleZeroAmount() {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(100L)
                .totalAmount(BigDecimal.ZERO)
                .build();

        assertThat(event.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void builder_shouldHandleLargeAmount() {
        BigDecimal largeAmount = new BigDecimal("999999999.99");

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(100L)
                .totalAmount(largeAmount)
                .build();

        assertThat(event.getTotalAmount()).isEqualByComparingTo(largeAmount);
    }

    @Test
    void builder_shouldHandleMultipleItems() {
        List<OrderItemEvent> items = Arrays.asList(
                createOrderItem(1L, "Item 1", "10.00", 1),
                createOrderItem(2L, "Item 2", "20.00", 2),
                createOrderItem(3L, "Item 3", "30.00", 3)
        );

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(100L)
                .items(items)
                .build();

        assertThat(event.getItems()).hasSize(3);
        assertThat(event.getItems().get(0).getItemId()).isEqualTo(1L);
        assertThat(event.getItems().get(1).getItemId()).isEqualTo(2L);
        assertThat(event.getItems().get(2).getItemId()).isEqualTo(3L);
    }

    @Test
    void setEventTimestamp_shouldUpdateTimestamp() {
        OrderCreatedEvent event = new OrderCreatedEvent();
        LocalDateTime newTimestamp = LocalDateTime.now().plusDays(1);

        event.setEventTimestamp(newTimestamp);

        assertThat(event.getEventTimestamp()).isEqualTo(newTimestamp);
    }

    @Test
    void builder_shouldCreateIndependentObjects() {
        List<OrderItemEvent> items = createTestItems();

        OrderCreatedEvent event1 = OrderCreatedEvent.builder()
                .orderId(100L)
                .items(items)
                .build();

        OrderCreatedEvent event2 = OrderCreatedEvent.builder()
                .orderId(200L)
                .items(items)
                .build();

        assertThat(event1.getOrderId()).isEqualTo(100L);
        assertThat(event2.getOrderId()).isEqualTo(200L);
        assertThat(event1.getItems()).isSameAs(event2.getItems());
    }

    private List<OrderItemEvent> createTestItems() {
        return Arrays.asList(
                createOrderItem(1L, "Test Item 1", "50.00", 1),
                createOrderItem(2L, "Test Item 2", "100.00", 2)
        );
    }

    private OrderItemEvent createOrderItem(Long itemId, String name, String price, Integer quantity) {
        OrderItemEvent item = new OrderItemEvent();
        item.setItemId(itemId);
        item.setItemName(name);
        item.setPrice(new BigDecimal(price));
        item.setQuantity(quantity);
        return item;
    }
}