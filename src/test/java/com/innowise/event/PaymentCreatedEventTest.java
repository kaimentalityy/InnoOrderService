package com.innowise.event;

import com.innowise.model.enums.EventType;
import com.innowise.model.enums.PaymentStatus;
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
        EventType eventType = EventType.CREATE_PAYMENT;
        LocalDateTime timestamp = LocalDateTime.now();
        String paymentId = "payment-10";
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

    @Test
    void builder_shouldCreateEventWithAllFields() {
        String eventId = "event-123";
        EventType eventType = EventType.CREATE_PAYMENT;
        LocalDateTime timestamp = LocalDateTime.now();
        String paymentId = "payment-456";
        Long orderId = 100L;
        Long userId = 200L;
        BigDecimal amount = new BigDecimal("150.00");
        PaymentStatus status = PaymentStatus.SUCCESS;

        PaymentCreatedEvent event = PaymentCreatedEvent.builder()
                .eventId(eventId)
                .eventType(eventType)
                .eventTimestamp(timestamp)
                .paymentId(paymentId)
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .status(status)
                .build();

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
    void create_shouldGenerateEventWithDefaultValues() {
        String paymentId = "payment-789";
        Long orderId = 300L;
        Long userId = 400L;
        BigDecimal amount = new BigDecimal("250.50");
        PaymentStatus status = PaymentStatus.SUCCESS;

        PaymentCreatedEvent event = PaymentCreatedEvent.create(
                paymentId, orderId, userId, amount, status
        );

        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventType()).isEqualTo(EventType.CREATE_PAYMENT);
        assertThat(event.getEventTimestamp()).isNotNull();
        assertThat(event.getPaymentId()).isEqualTo(paymentId);
        assertThat(event.getOrderId()).isEqualTo(orderId);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getAmount()).isEqualByComparingTo(amount);
        assertThat(event.getStatus()).isEqualTo(status);
    }

    @Test
    void create_shouldGenerateUniqueEventIds() {
        String paymentId = "payment-123";
        Long orderId = 100L;
        Long userId = 200L;
        BigDecimal amount = new BigDecimal("100.00");
        PaymentStatus status = PaymentStatus.SUCCESS;

        PaymentCreatedEvent event1 = PaymentCreatedEvent.create(
                paymentId, orderId, userId, amount, status
        );
        PaymentCreatedEvent event2 = PaymentCreatedEvent.create(
                paymentId, orderId, userId, amount, status
        );

        assertThat(event1.getEventId()).isNotEqualTo(event2.getEventId());
    }

    @Test
    void allArgsConstructor_shouldCreateEventWithAllFields() {
        String eventId = "event-all";
        EventType eventType = EventType.CREATE_PAYMENT;
        LocalDateTime timestamp = LocalDateTime.now();
        String paymentId = "payment-all";
        Long orderId = 500L;
        Long userId = 600L;
        BigDecimal amount = new BigDecimal("999.99");
        PaymentStatus status = PaymentStatus.SUCCESS;

        PaymentCreatedEvent event = new PaymentCreatedEvent(
                eventId, eventType, timestamp, paymentId, orderId, userId, amount, status
        );

        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getEventTimestamp()).isEqualTo(timestamp);
        assertThat(event.getPaymentId()).isEqualTo(paymentId);
        assertThat(event.getOrderId()).isEqualTo(orderId);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getAmount()).isEqualByComparingTo(amount);
        assertThat(event.getStatus()).isEqualTo(status);
    }
}