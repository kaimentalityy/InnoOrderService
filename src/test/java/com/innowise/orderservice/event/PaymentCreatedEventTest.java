package com.innowise.orderservice.event;

import com.innowise.orderservice.model.enums.EventType;
import com.innowise.orderservice.model.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentCreatedEventTest {

    @Test
    void defaultConstructor_shouldInitializeAllFieldsToNull() {
        PaymentCreatedEvent event = new PaymentCreatedEvent();

        assertThat(event.getEventId()).isNull();
        assertThat(event.getEventType()).isNull();
        assertThat(event.getEventTimestamp()).isNull();
        assertThat(event.getPaymentId()).isNull();
        assertThat(event.getOrderId()).isNull();
        assertThat(event.getUserId()).isNull();
        assertThat(event.getAmount()).isNull();
        assertThat(event.getStatus()).isNull();
    }

    @Test
    void setters_shouldAssignValuesCorrectly() {
        PaymentCreatedEvent event = new PaymentCreatedEvent();

        String eventId = "123";
        EventType eventType = EventType.PAYMENT_CREATED;
        LocalDateTime timestamp = LocalDateTime.now();
        Long paymentId = 10L;
        Long orderId = 20L;
        Long userId = 30L;
        BigDecimal amount = new BigDecimal("99.99");
        PaymentStatus status = PaymentStatus.SUCCESS;

        event.setEventId(eventId);
        event.setEventType(eventType);
        event.setEventTimestamp(timestamp);
        event.setPaymentId(paymentId);
        event.setOrderId(orderId);
        event.setUserId(userId);
        event.setAmount(amount);
        event.setStatus(status);

        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getEventTimestamp()).isEqualTo(timestamp);
        assertThat(event.getPaymentId()).isEqualTo(paymentId);
        assertThat(event.getOrderId()).isEqualTo(orderId);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getAmount()).isEqualByComparingTo(amount);
        assertThat(event.getStatus()).isEqualTo(status);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        PaymentCreatedEvent event1 = new PaymentCreatedEvent();
        PaymentCreatedEvent event2 = new PaymentCreatedEvent();

        event1.setEventId("id1");
        event2.setEventId("id1");

        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    void toString_shouldContainFieldNames() {
        PaymentCreatedEvent event = new PaymentCreatedEvent();
        event.setEventId("abc");

        String toString = event.toString();

        assertThat(toString).contains("abc");
        assertThat(toString).contains("eventId");
    }
}
