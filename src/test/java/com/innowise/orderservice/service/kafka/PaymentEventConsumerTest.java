package com.innowise.orderservice.service.kafka;

import com.innowise.orderservice.dao.repository.OrderRepository;
import com.innowise.orderservice.event.PaymentCreatedEvent;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.enums.OrderStatus;
import com.innowise.orderservice.model.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentEventConsumer Tests")
class PaymentEventConsumerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    private static final String TOPIC = "order-events";
    private static final String KEY = "order-key-123";
    private static final int PARTITION = 0;
    private static final long OFFSET = 100L;
    private static final Long ORDER_ID = 1L;
    private static final Long PAYMENT_ID = 100L;

    private Order order;
    private PaymentCreatedEvent event;

    @BeforeEach
    void setUp() {
        order = createOrder(ORDER_ID, OrderStatus.PAYMENT_PENDING);
        event = createPaymentEvent(ORDER_ID, PAYMENT_ID, PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("Should successfully update order status to CONFIRMED when payment is SUCCESS")
    void handlePaymentCreatedEvent_PaymentSuccess_UpdatesOrderToConfirmed() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(savedOrder.getId()).isEqualTo(ORDER_ID);
    }

    @Test
    @DisplayName("Should successfully update order status to CANCELLED when payment is FAILED")
    void handlePaymentCreatedEvent_PaymentFailed_UpdatesOrderToCancelled() {
        event = createPaymentEvent(ORDER_ID, PAYMENT_ID, PaymentStatus.FAILED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @ParameterizedTest
    @MethodSource("providePaymentStatusToOrderStatusMappings")
    @DisplayName("Should correctly map payment status to order status")
    void handlePaymentCreatedEvent_AllPaymentStatuses_MapsCorrectly(
            PaymentStatus paymentStatus, OrderStatus expectedOrderStatus, OrderStatus initialStatus) {
        order.setStatus(initialStatus);
        event = createPaymentEvent(ORDER_ID, PAYMENT_ID, paymentStatus);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(expectedOrderStatus);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist")
    void handlePaymentCreatedEvent_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw NullPointerException when event is null")
    void handlePaymentCreatedEvent_NullEvent_ThrowsException() {
        assertThatThrownBy(() -> paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, null))
                .isInstanceOf(NullPointerException.class);

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when order ID is null")
    void handlePaymentCreatedEvent_NullOrderId_ThrowsException() {
        event = createPaymentEvent(null, PAYMENT_ID, PaymentStatus.SUCCESS);

        assertThatThrownBy(() -> paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order ID cannot be null");

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when payment ID is null")
    void handlePaymentCreatedEvent_NullPaymentId_ThrowsException() {
        event = createPaymentEvent(ORDER_ID, null, PaymentStatus.SUCCESS);

        assertThatThrownBy(() -> paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment ID cannot be null");

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when payment status is null")
    void handlePaymentCreatedEvent_NullPaymentStatus_ThrowsException() {
        event = createPaymentEvent(ORDER_ID, PAYMENT_ID, null);

        assertThatThrownBy(() -> paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment status cannot be null");

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should not save order when status is already the same")
    void handlePaymentCreatedEvent_SameStatus_DoesNotSaveOrder() {
        order.setStatus(OrderStatus.CONFIRMED);
        event = createPaymentEvent(ORDER_ID, PAYMENT_ID, PaymentStatus.SUCCESS);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should not save order when CANCELLED status matches FAILED payment")
    void handlePaymentCreatedEvent_AlreadyCancelled_DoesNotSaveOrder() {
        order.setStatus(OrderStatus.CANCELLED);
        event = createPaymentEvent(ORDER_ID, PAYMENT_ID, PaymentStatus.FAILED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should not save order when PAYMENT_PENDING status matches PENDING payment")
    void handlePaymentCreatedEvent_AlreadyPaymentPending_DoesNotSaveOrder() {
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        event = createPaymentEvent(ORDER_ID, PAYMENT_ID, PaymentStatus.PENDING);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order from PENDING to CONFIRMED")
    void handlePaymentCreatedEvent_PendingToConfirmed_UpdatesSuccessfully() {
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should update order from PAYMENT_PENDING to CONFIRMED")
    void handlePaymentCreatedEvent_PaymentPendingToConfirmed_UpdatesSuccessfully() {
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should handle event with all Kafka headers present")
    void handlePaymentCreatedEvent_AllKafkaHeaders_ProcessesSuccessfully() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        paymentEventConsumer.handlePaymentCreatedEvent(
                "test-topic", "test-key", 5, 999L, event);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should verify order repository is called exactly once for findById")
    void handlePaymentCreatedEvent_VerifyRepositoryInteractions() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        paymentEventConsumer.handlePaymentCreatedEvent(TOPIC, KEY, PARTITION, OFFSET, event);

        verify(orderRepository, times(1)).findById(ORDER_ID);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Should handle DLT event successfully (log error)")
    void handlePaymentEventsDlt_LogsError() {
        paymentEventConsumer.handlePaymentEventsDlt(TOPIC, KEY, PARTITION, OFFSET, event);

        verifyNoInteractions(orderRepository);
    }

    private static Stream<Arguments> providePaymentStatusToOrderStatusMappings() {
        return Stream.of(
                Arguments.of(PaymentStatus.SUCCESS, OrderStatus.CONFIRMED, OrderStatus.PAYMENT_PENDING),
                Arguments.of(PaymentStatus.FAILED, OrderStatus.CANCELLED, OrderStatus.PAYMENT_PENDING));
    }

    private Order createOrder(Long id, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        return order;
    }

    private PaymentCreatedEvent createPaymentEvent(Long orderId, Long paymentId, PaymentStatus status) {
        PaymentCreatedEvent event = new PaymentCreatedEvent();
        event.setOrderId(orderId);
        event.setPaymentId(paymentId);
        event.setStatus(status);
        return event;
    }
}