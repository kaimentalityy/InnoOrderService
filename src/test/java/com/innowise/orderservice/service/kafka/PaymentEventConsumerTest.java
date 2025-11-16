package com.innowise.orderservice.service.kafka;

import com.innowise.orderservice.dao.repository.OrderRepository;
import com.innowise.orderservice.event.PaymentCreatedEvent;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.enums.OrderStatus;
import com.innowise.orderservice.model.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private Order testOrder;
    private PaymentCreatedEvent successEvent;
    private PaymentCreatedEvent failedEvent;
    private PaymentCreatedEvent pendingEvent;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(100L);
        testOrder.setUserId(200L);
        testOrder.setStatus(OrderStatus.PAYMENT_PENDING);
        testOrder.setCreatedDate(LocalDateTime.now());
        testOrder.setItems(new ArrayList<>());

        successEvent = PaymentCreatedEvent.builder()
                .eventId("event-success-1")
                .paymentId(123L)
                .orderId(100L)
                .userId(200L)
                .amount(new BigDecimal("150.00"))
                .status(PaymentStatus.SUCCESS)
                .eventTimestamp(LocalDateTime.now())
                .build();

        failedEvent = PaymentCreatedEvent.builder()
                .eventId("event-failed-1")
                .paymentId(456L)
                .orderId(100L)
                .userId(200L)
                .amount(new BigDecimal("150.00"))
                .status(PaymentStatus.FAILED)
                .eventTimestamp(LocalDateTime.now())
                .build();

        pendingEvent = PaymentCreatedEvent.builder()
                .eventId("event-pending-1")
                .paymentId(789L)
                .orderId(100L)
                .userId(200L)
                .amount(new BigDecimal("150.00"))
                .status(PaymentStatus.PENDING)
                .eventTimestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void handlePaymentCreatedEvent_shouldUpdateOrderToConfirmed_whenPaymentSuccess() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(successEvent);

        verify(orderRepository).findById(100L);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void handlePaymentCreatedEvent_shouldUpdateOrderToCancelled_whenPaymentFailed() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(failedEvent);

        verify(orderRepository).findById(100L);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void handlePaymentCreatedEvent_shouldUpdateOrderToPaymentPending_whenPaymentPending() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(pendingEvent);

        verify(orderRepository).findById(100L);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
    }

    @Test
    void handlePaymentCreatedEvent_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentEventConsumer.handlePaymentCreatedEvent(successEvent))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository).findById(100L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void handlePaymentCreatedEvent_shouldSkipDuplicateEvent() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(successEvent);
        paymentEventConsumer.handlePaymentCreatedEvent(successEvent);

        verify(orderRepository, times(1)).findById(100L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void handlePaymentCreatedEvent_shouldProcessMultipleDifferentEvents() {
        PaymentCreatedEvent event1 = PaymentCreatedEvent.builder()
                .eventId("event-1")
                .orderId(100L)
                .status(PaymentStatus.SUCCESS)
                .build();

        PaymentCreatedEvent event2 = PaymentCreatedEvent.builder()
                .eventId("event-2")
                .orderId(100L)
                .status(PaymentStatus.FAILED)
                .build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(event1);
        paymentEventConsumer.handlePaymentCreatedEvent(event2);

        verify(orderRepository, times(2)).findById(100L);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void handlePaymentCreatedEvent_shouldRemoveFromProcessedEvents_whenExceptionOccurs() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> paymentEventConsumer.handlePaymentCreatedEvent(successEvent))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(successEvent);

        verify(orderRepository, times(2)).findById(100L);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void handlePaymentCreatedEvent_shouldHandleNullEventId() {
        PaymentCreatedEvent eventWithNullId = PaymentCreatedEvent.builder()
                .eventId(null)
                .orderId(100L)
                .status(PaymentStatus.SUCCESS)
                .build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(eventWithNullId);
        paymentEventConsumer.handlePaymentCreatedEvent(eventWithNullId);

        verify(orderRepository, times(2)).findById(100L);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void handlePaymentCreatedEvent_shouldCallFindById() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(successEvent);

        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    void handlePaymentCreatedEvent_shouldCallSave() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(successEvent);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void handlePaymentCreatedEvent_shouldPreserveOrderFields() {
        Order orderWithDetails = new Order();
        orderWithDetails.setId(100L);
        orderWithDetails.setUserId(200L);
        orderWithDetails.setStatus(OrderStatus.PAYMENT_PENDING);
        orderWithDetails.setCreatedDate(LocalDateTime.now());
        orderWithDetails.setItems(new ArrayList<>());

        when(orderRepository.findById(100L)).thenReturn(Optional.of(orderWithDetails));
        when(orderRepository.save(any(Order.class))).thenReturn(orderWithDetails);

        paymentEventConsumer.handlePaymentCreatedEvent(successEvent);

        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getId()).isEqualTo(100L);
        assertThat(savedOrder.getUserId()).isEqualTo(200L);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(savedOrder.getCreatedDate()).isNotNull();
    }

    @Test
    void handlePaymentCreatedEvent_shouldHandleOrderNotFoundException_andRethrow() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentEventConsumer.handlePaymentCreatedEvent(successEvent))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository).findById(100L);
    }

    @Test
    void handlePaymentCreatedEvent_shouldMapPaymentStatusCorrectly() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(successEvent);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        reset(orderRepository);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(failedEvent);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CANCELLED);

        reset(orderRepository);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(pendingEvent);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
    }

    @Test
    void handlePaymentCreatedEvent_shouldHandleDifferentOrderIds() {
        Order order1 = new Order();
        order1.setId(100L);
        order1.setStatus(OrderStatus.PAYMENT_PENDING);

        Order order2 = new Order();
        order2.setId(200L);
        order2.setStatus(OrderStatus.PAYMENT_PENDING);

        PaymentCreatedEvent event1 = PaymentCreatedEvent.builder()
                .eventId("event-order-1")
                .orderId(100L)
                .status(PaymentStatus.SUCCESS)
                .build();

        PaymentCreatedEvent event2 = PaymentCreatedEvent.builder()
                .eventId("event-order-2")
                .orderId(200L)
                .status(PaymentStatus.FAILED)
                .build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order1));
        when(orderRepository.findById(200L)).thenReturn(Optional.of(order2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentEventConsumer.handlePaymentCreatedEvent(event1);
        paymentEventConsumer.handlePaymentCreatedEvent(event2);

        verify(orderRepository).findById(100L);
        verify(orderRepository).findById(200L);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void handlePaymentCreatedEvent_shouldHandleRepositoryException() {
        when(orderRepository.findById(100L))
                .thenThrow(new RuntimeException("Database connection error"));

        assertThatThrownBy(() -> paymentEventConsumer.handlePaymentCreatedEvent(successEvent))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection error");

        verify(orderRepository).findById(100L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void handlePaymentCreatedEvent_shouldHandleEventWithDifferentPaymentStatuses() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        for (PaymentStatus status : PaymentStatus.values()) {
            PaymentCreatedEvent event = PaymentCreatedEvent.builder()
                    .eventId("event-" + status.name())
                    .orderId(100L)
                    .status(status)
                    .build();

            paymentEventConsumer.handlePaymentCreatedEvent(event);
        }

        verify(orderRepository, times(PaymentStatus.values().length)).save(any(Order.class));
    }

    @Test
    void handlePaymentCreatedEvent_shouldUpdateOrderFromAnyStatus() {
        Order confirmedOrder = new Order();
        confirmedOrder.setId(100L);
        confirmedOrder.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(100L)).thenReturn(Optional.of(confirmedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(confirmedOrder);

        paymentEventConsumer.handlePaymentCreatedEvent(failedEvent);

        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}