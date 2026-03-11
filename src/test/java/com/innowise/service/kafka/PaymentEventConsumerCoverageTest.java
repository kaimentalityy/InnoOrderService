package com.innowise.service.kafka;

import com.innowise.dao.repository.OrderRepository;
import com.innowise.event.PaymentCreatedEvent;
import com.innowise.model.entity.Order;
import com.innowise.model.enums.OrderStatus;
import com.innowise.model.enums.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Additional coverage tests for PaymentEventConsumer edge cases.
 */
@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerCoverageTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentEventConsumer consumer;

    private static final String TOPIC = "test-topic";
    private static final String KEY = "key-1";
    private static final int PARTITION = 0;
    private static final long OFFSET = 1L;

    @Test
    @DisplayName("Should ignore PENDING event when order is already CONFIRMED")
    void handle_confirmedOrder_pendingPayment_shouldNotSave() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);

        PaymentCreatedEvent event = new PaymentCreatedEvent();
        event.setOrderId(1L);
        event.setPaymentId("pay-1");
        event.setStatus(PaymentStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        consumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should ignore PENDING event when order is already CANCELLED")
    void handle_cancelledOrder_pendingPayment_shouldNotSave() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CANCELLED);

        PaymentCreatedEvent event = new PaymentCreatedEvent();
        event.setOrderId(1L);
        event.setPaymentId("pay-2");
        event.setStatus(PaymentStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        consumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update from CONFIRMED to CANCELLED (non-pending transition)")
    void handle_confirmedOrder_failedPayment_shouldUpdate() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);

        PaymentCreatedEvent event = new PaymentCreatedEvent();
        event.setOrderId(1L);
        event.setPaymentId("pay-3");
        event.setStatus(PaymentStatus.FAILED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        consumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("handlePaymentEventsDlt - should simply log and not interact with repository")
    void handleDlt_shouldOnlyLog() {
        PaymentCreatedEvent event = new PaymentCreatedEvent();
        event.setOrderId(99L);
        event.setPaymentId("pay-99");
        event.setStatus(PaymentStatus.FAILED);

        consumer.handlePaymentEventsDlt(TOPIC, KEY, PARTITION, OFFSET, event);

        verifyNoInteractions(orderRepository);
    }
}
