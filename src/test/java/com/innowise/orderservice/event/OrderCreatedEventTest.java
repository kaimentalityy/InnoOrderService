package com.innowise.orderservice.event;

import com.innowise.orderservice.model.enums.EventType;
import com.innowise.orderservice.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderCreatedEvent Tests")
class OrderCreatedEventTest {

    @Test
    @DisplayName("Should create event with no-args constructor and initialize default values")
    void testNoArgsConstructor() {
        
        OrderCreatedEvent event = new OrderCreatedEvent();

        
        assertNotNull(event.getEventId(), "Event ID should be initialized");
        assertFalse(event.getEventId().isEmpty(), "Event ID should not be empty");
        assertEquals(EventType.ORDER_CREATE, event.getEventType(), "Event type should be ORDER_CREATE");
        assertNotNull(event.getEventTimestamp(), "Event timestamp should be initialized");
        assertNull(event.getOrderId(), "Order ID should be null");
        assertNull(event.getUserId(), "User ID should be null");
        assertNull(event.getStatus(), "Status should be null");
        assertNull(event.getTotalAmount(), "Total amount should be null");
        assertNull(event.getItems(), "Items should be null");
    }

    @Test
    @DisplayName("Should create event with all-args constructor")
    void testAllArgsConstructor() {
        Long orderId = 123L;
        Long userId = 456L;
        OrderStatus status = OrderStatus.PAYMENT_PENDING;
        BigDecimal totalAmount = new BigDecimal("99.99");
        List<OrderItemEvent> items = Arrays.asList(
                createOrderItemEvent(1L, 2),
                createOrderItemEvent(2L, 1)
        );

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, status, totalAmount, items);

        assertNotNull(event.getEventId(), "Event ID should be initialized");
        assertEquals(EventType.ORDER_CREATE, event.getEventType(), "Event type should be ORDER_CREATE");
        assertNotNull(event.getEventTimestamp(), "Event timestamp should be initialized");
        assertEquals(orderId, event.getOrderId(), "Order ID should match");
        assertEquals(userId, event.getUserId(), "User ID should match");
        assertEquals(status, event.getStatus(), "Status should match");
        assertEquals(totalAmount, event.getTotalAmount(), "Total amount should match");
        assertEquals(items, event.getItems(), "Items should match");
        assertEquals(2, event.getItems().size(), "Items list size should be 2");
    }

    @Test
    @DisplayName("Should generate unique event IDs for different instances")
    void testUniqueEventIds() {
        OrderCreatedEvent event1 = new OrderCreatedEvent();
        OrderCreatedEvent event2 = new OrderCreatedEvent();

        assertNotEquals(event1.getEventId(), event2.getEventId(),
                "Event IDs should be unique");
    }

    @Test
    @DisplayName("Should generate valid UUID format for event ID")
    void testEventIdIsValidUUID() {
        OrderCreatedEvent event = new OrderCreatedEvent();

        assertDoesNotThrow(() -> java.util.UUID.fromString(event.getEventId()),
                "Event ID should be a valid UUID");
    }

    @Test
    @DisplayName("Should set event timestamp close to current time")
    void testEventTimestampIsRecent() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        OrderCreatedEvent event = new OrderCreatedEvent();

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertTrue(event.getEventTimestamp().isAfter(before),
                "Event timestamp should be after the before time");
        assertTrue(event.getEventTimestamp().isBefore(after),
                "Event timestamp should be before the after time");
    }

    @Test
    @DisplayName("Should set and get orderId correctly")
    void testSetAndGetOrderId() {
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        Long orderId = 999L;

        
        event.setOrderId(orderId);

        
        assertEquals(orderId, event.getOrderId(), "Order ID should match the set value");
    }

    @Test
    @DisplayName("Should set and get userId correctly")
    void testSetAndGetUserId() {
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        Long userId = 777L;

        
        event.setUserId(userId);

        
        assertEquals(userId, event.getUserId(), "User ID should match the set value");
    }

    @Test
    @DisplayName("Should set and get status correctly")
    void testSetAndGetStatus() {
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        OrderStatus status = OrderStatus.CONFIRMED;

        
        event.setStatus(status);

        
        assertEquals(status, event.getStatus(), "Status should match the set value");
    }

    @Test
    @DisplayName("Should set and get totalAmount correctly")
    void testSetAndGetTotalAmount() {
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        BigDecimal amount = new BigDecimal("150.75");

        
        event.setTotalAmount(amount);

        
        assertEquals(amount, event.getTotalAmount(), "Total amount should match the set value");
    }

    @Test
    @DisplayName("Should set and get items correctly")
    void testSetAndGetItems() {
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        List<OrderItemEvent> items = Arrays.asList(createOrderItemEvent(1L, 3));

        
        event.setItems(items);

        
        assertEquals(items, event.getItems(), "Items should match the set value");
        assertEquals(1, event.getItems().size(), "Items list should have 1 element");
    }

    @Test
    @DisplayName("Should set and get eventId correctly")
    void testSetAndGetEventId() {
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        String customEventId = "custom-event-id-123";

        
        event.setEventId(customEventId);

        
        assertEquals(customEventId, event.getEventId(), "Event ID should match the set value");
    }

    @Test
    @DisplayName("Should set and get eventType correctly")
    void testSetAndGetEventType() {
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        EventType customEventType = EventType.ORDER_CREATE;

        
        event.setEventType(customEventType);

        
        assertEquals(customEventType, event.getEventType(), "Event type should match the set value");
    }

    @Test
    @DisplayName("Should set and get eventTimestamp correctly")
    void testSetAndGetEventTimestamp() {
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        LocalDateTime customTimestamp = LocalDateTime.of(2024, 1, 1, 12, 0);

        
        event.setEventTimestamp(customTimestamp);

        
        assertEquals(customTimestamp, event.getEventTimestamp(),
                "Event timestamp should match the set value");
    }

    @Test
    @DisplayName("Should handle null values in all-args constructor")
    void testAllArgsConstructorWithNulls() {
        
        OrderCreatedEvent event = new OrderCreatedEvent(null, null, null, null, null);

        
        assertNotNull(event.getEventId(), "Event ID should still be initialized");
        assertEquals(EventType.ORDER_CREATE, event.getEventType(),
                "Event type should still be ORDER_CREATE");
        assertNotNull(event.getEventTimestamp(), "Event timestamp should still be initialized");
        assertNull(event.getOrderId(), "Order ID should be null");
        assertNull(event.getUserId(), "User ID should be null");
        assertNull(event.getStatus(), "Status should be null");
        assertNull(event.getTotalAmount(), "Total amount should be null");
        assertNull(event.getItems(), "Items should be null");
    }

    @Test
    @DisplayName("Should handle empty items list")
    void testWithEmptyItemsList() {
        List<OrderItemEvent> emptyItems = Arrays.asList();

        OrderCreatedEvent event = new OrderCreatedEvent(1L, 2L, OrderStatus.PAYMENT_PENDING,
                new BigDecimal("0.00"), emptyItems);

        assertNotNull(event.getItems(), "Items should not be null");
        assertTrue(event.getItems().isEmpty(), "Items list should be empty");
    }

    @Test
    @DisplayName("Should handle BigDecimal with different scales")
    void testBigDecimalWithDifferentScales() {
        BigDecimal amount1 = new BigDecimal("100");
        BigDecimal amount2 = new BigDecimal("100.00");
        BigDecimal amount3 = new BigDecimal("100.999");

        OrderCreatedEvent event1 = new OrderCreatedEvent(1L, 2L, OrderStatus.PAYMENT_PENDING, amount1, null);
        OrderCreatedEvent event2 = new OrderCreatedEvent(1L, 2L, OrderStatus.PAYMENT_PENDING, amount2, null);
        OrderCreatedEvent event3 = new OrderCreatedEvent(1L, 2L, OrderStatus.PAYMENT_PENDING, amount3, null);

        assertEquals(0, event1.getTotalAmount().compareTo(new BigDecimal("100")));
        assertEquals(0, event2.getTotalAmount().compareTo(new BigDecimal("100.00")));
        assertEquals(0, event3.getTotalAmount().compareTo(new BigDecimal("100.999")));
    }

    @Test
    @DisplayName("Should handle large order and user IDs")
    void testWithLargeIds() {
        Long largeOrderId = Long.MAX_VALUE;
        Long largeUserId = Long.MAX_VALUE - 1;

        OrderCreatedEvent event = new OrderCreatedEvent(largeOrderId, largeUserId,
                OrderStatus.PAYMENT_PENDING, BigDecimal.ZERO, null);

        assertEquals(largeOrderId, event.getOrderId(), "Should handle max Long value");
        assertEquals(largeUserId, event.getUserId(), "Should handle large Long value");
    }

    @Test
    @DisplayName("Should preserve items list reference")
    void testItemsListReference() {
        List<OrderItemEvent> items = Arrays.asList(createOrderItemEvent(1L, 2));
        OrderCreatedEvent event = new OrderCreatedEvent();

        event.setItems(items);

        assertSame(items, event.getItems(), "Should preserve the same list reference");
    }

    private OrderItemEvent createOrderItemEvent(Long productId, int quantity) {
        OrderItemEvent item = new OrderItemEvent();
        return item;
    }
}