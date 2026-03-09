package com.innowise.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnumsTest {


    @Test
    void eventType_values() {
        EventType[] values = EventType.values();
        assertThat(values).containsExactly(EventType.CREATE_PAYMENT, EventType.ORDER_CREATE);
    }

    @Test
    void eventType_valueOf() {
        assertThat(EventType.valueOf("CREATE_PAYMENT")).isEqualTo(EventType.CREATE_PAYMENT);
        assertThat(EventType.valueOf("ORDER_CREATE")).isEqualTo(EventType.ORDER_CREATE);
    }


    @Test
    void orderStatus_values() {
        OrderStatus[] values = OrderStatus.values();
        assertThat(values).containsExactly(
                OrderStatus.PAYMENT_PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.CANCELLED);
    }

    @Test
    void orderStatus_valueOf() {
        assertThat(OrderStatus.valueOf("PAYMENT_PENDING")).isEqualTo(OrderStatus.PAYMENT_PENDING);
        assertThat(OrderStatus.valueOf("CONFIRMED")).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(OrderStatus.valueOf("CANCELLED")).isEqualTo(OrderStatus.CANCELLED);
    }


    @Test
    void paymentStatus_values() {
        PaymentStatus[] values = PaymentStatus.values();
        assertThat(values).containsExactly(
                PaymentStatus.PENDING,
                PaymentStatus.SUCCESS,
                PaymentStatus.FAILED);
    }

    @Test
    void paymentStatus_valueOf() {
        assertThat(PaymentStatus.valueOf("PENDING")).isEqualTo(PaymentStatus.PENDING);
        assertThat(PaymentStatus.valueOf("SUCCESS")).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(PaymentStatus.valueOf("FAILED")).isEqualTo(PaymentStatus.FAILED);
    }
}
