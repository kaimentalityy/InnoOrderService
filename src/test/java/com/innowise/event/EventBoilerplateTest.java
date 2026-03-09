package com.innowise.event;

import com.innowise.model.enums.EventType;
import com.innowise.model.enums.OrderStatus;
import com.innowise.model.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventBoilerplateTest {

    @Test
    void orderCreatedEventBoilerplate() {
        LocalDateTime now = LocalDateTime.now();
        OrderItemEvent item = new OrderItemEvent(1L, "Item", BigDecimal.TEN, 2);
        OrderCreatedEvent event1 = OrderCreatedEvent.builder()
                .eventId("test-id")
                .orderId(1L)
                .userId("user1")
                .status(OrderStatus.PAYMENT_PENDING)
                .eventTimestamp(now)
                .items(List.of(item))
                .build();

        OrderCreatedEvent event2 = OrderCreatedEvent.builder()
                .eventId("test-id")
                .orderId(1L)
                .userId("user1")
                .status(OrderStatus.PAYMENT_PENDING)
                .eventTimestamp(now)
                .items(List.of(item))
                .build();

        assertThat(event1.getOrderId()).isEqualTo(1L);
        assertThat(event1.getUserId()).isEqualTo("user1");
        assertThat(event1.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
        assertThat(event1.getEventTimestamp()).isEqualTo(now);
        assertThat(event1.getItems()).hasSize(1);

        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        assertThat(event1.toString()).contains("OrderCreatedEvent");

        OrderCreatedEvent empty = new OrderCreatedEvent();
        empty.setOrderId(5L);
        assertThat(empty.getOrderId()).isEqualTo(5L);
    }

    @Test
    void orderItemEventBoilerplate() {
        OrderItemEvent item1 = new OrderItemEvent(1L, "A", BigDecimal.ONE, 1);
        OrderItemEvent item2 = new OrderItemEvent(1L, "A", BigDecimal.ONE, 1);

        assertThat(item1.getItemId()).isEqualTo(1L);
        assertThat(item1.getItemName()).isEqualTo("A");
        assertThat(item1.getPrice()).isEqualTo(BigDecimal.ONE);
        assertThat(item1.getQuantity()).isEqualTo(1);

        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());

        OrderItemEvent empty = new OrderItemEvent();
        empty.setItemName("Test");
        assertThat(empty.getItemName()).isEqualTo("Test");
    }

    @Test
    void paymentCreatedEventBoilerplate() {
        LocalDateTime now = LocalDateTime.now();
        PaymentCreatedEvent event1 = PaymentCreatedEvent.create("p1", 100L, "user1", BigDecimal.TEN,
                PaymentStatus.PENDING);
        PaymentCreatedEvent event2 = PaymentCreatedEvent.create("p1", 100L, "user1", BigDecimal.TEN,
                PaymentStatus.PENDING);

        event1.setEventTimestamp(now);
        event2.setEventTimestamp(now);
        event1.setEventId("e1");
        event2.setEventId("e1");
        event1.setEventType(EventType.CREATE_PAYMENT);
        event2.setEventType(EventType.CREATE_PAYMENT);

        assertThat(event1.getPaymentId()).isEqualTo("p1");
        assertThat(event1.getOrderId()).isEqualTo(100L);
        assertThat(event1.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(event1.getUserId()).isEqualTo("user1");
        assertThat(event1.getStatus()).isEqualTo(PaymentStatus.PENDING);

        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());

        PaymentCreatedEvent empty = new PaymentCreatedEvent();
        empty.setPaymentId("p10");
        assertThat(empty.getPaymentId()).isEqualTo("p10");
    }
}
