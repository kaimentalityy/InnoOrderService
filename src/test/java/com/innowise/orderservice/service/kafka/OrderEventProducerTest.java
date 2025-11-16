package com.innowise.orderservice.service.kafka;

import com.innowise.orderservice.event.OrderCreatedEvent;
import com.innowise.orderservice.event.OrderItemEvent;
import com.innowise.orderservice.model.enums.OrderStatus;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderEventProducer orderEventProducer;

    @Captor
    private ArgumentCaptor<String> topicCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<OrderCreatedEvent> eventCaptor;

    private OrderCreatedEvent testEvent;
    private CompletableFuture<SendResult<String, Object>> future;

    @BeforeEach
    void setUp() {
        testEvent = new OrderCreatedEvent(
                100L,
                200L,
                OrderStatus.PAYMENT_PENDING,
                new BigDecimal("150.00"),
                createTestItems()
        );

        future = new CompletableFuture<>();
    }

    @Test
    void constructor_shouldInitializeKafkaTemplate() {
        KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
        OrderEventProducer producer = new OrderEventProducer(template);

        assertThat(producer).isNotNull();
    }

    @Test
    void sendOrderCreatedEvent_shouldSendEventSuccessfully() {
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("order-events", 0),
                0L,
                0,
                System.currentTimeMillis(),
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "order-events",
                "100",
                testEvent
        );
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, metadata);
        future.complete(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);

        orderEventProducer.sendOrderCreatedEvent(testEvent);

        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                eventCaptor.capture()
        );

        assertThat(topicCaptor.getValue()).isEqualTo("order-events");
        assertThat(keyCaptor.getValue()).isEqualTo("100");

        OrderCreatedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getOrderId()).isEqualTo(100L);
        assertThat(capturedEvent.getUserId()).isEqualTo(200L);
        assertThat(capturedEvent.getTotalAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(capturedEvent.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
    }

    @Test
    void sendOrderCreatedEvent_shouldHandleFailedSend() {
        RuntimeException exception = new RuntimeException("Kafka send failed");
        future.completeExceptionally(exception);

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);

        orderEventProducer.sendOrderCreatedEvent(testEvent);

        verify(kafkaTemplate).send(eq("order-events"), eq("100"), eq(testEvent));
    }

    @Test
    void sendOrderCreatedEvent_shouldHandleKafkaTemplateException() {
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("KafkaTemplate error"));

        orderEventProducer.sendOrderCreatedEvent(testEvent);

        verify(kafkaTemplate).send(eq("order-events"), eq("100"), eq(testEvent));
    }

    @Test
    void sendOrderCreatedEvent_shouldUseOrderIdAsKey() {
        OrderCreatedEvent eventWithDifferentOrderId = new OrderCreatedEvent(
                999L,
                888L,
                OrderStatus.CONFIRMED,
                new BigDecimal("500.00"),
                createTestItems()
        );

        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("order-events", 0),
                0L,
                0,
                System.currentTimeMillis(),
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "order-events",
                "999",
                eventWithDifferentOrderId
        );
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, metadata);
        future.complete(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);

        orderEventProducer.sendOrderCreatedEvent(eventWithDifferentOrderId);

        verify(kafkaTemplate).send(
                eq("order-events"),
                keyCaptor.capture(),
                eq(eventWithDifferentOrderId)
        );

        assertThat(keyCaptor.getValue()).isEqualTo("999");
    }

    @Test
    void sendOrderCreatedEvent_shouldSendToCorrectTopic() {
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("order-events", 0),
                0L,
                0,
                System.currentTimeMillis(),
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "order-events",
                "100",
                testEvent
        );
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, metadata);
        future.complete(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);

        orderEventProducer.sendOrderCreatedEvent(testEvent);

        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                anyString(),
                any()
        );

        assertThat(topicCaptor.getValue()).isEqualTo("order-events");
    }

    @Test
    void sendOrderCreatedEvent_shouldHandleSuccessfulSendWithMetadata() {
        long expectedOffset = 42L;
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("order-events", 0),
                expectedOffset,
                0,
                System.currentTimeMillis(),
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "order-events",
                "100",
                testEvent
        );
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, metadata);
        future.complete(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);

        orderEventProducer.sendOrderCreatedEvent(testEvent);

        verify(kafkaTemplate).send(eq("order-events"), eq("100"), eq(testEvent));

        assertThat(metadata.offset()).isEqualTo(expectedOffset);
    }

    @Test
    void sendOrderCreatedEvent_shouldHandleMultipleEvents() {
        OrderCreatedEvent event1 = createEvent(101L, OrderStatus.PAYMENT_PENDING);
        OrderCreatedEvent event2 = createEvent(102L, OrderStatus.CONFIRMED);
        OrderCreatedEvent event3 = createEvent(103L, OrderStatus.CANCELLED);

        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("order-events", 0),
                0L,
                0,
                System.currentTimeMillis(),
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "order-events",
                "100",
                testEvent
        );
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, metadata);

        CompletableFuture<SendResult<String, Object>> future1 = new CompletableFuture<>();
        CompletableFuture<SendResult<String, Object>> future2 = new CompletableFuture<>();
        CompletableFuture<SendResult<String, Object>> future3 = new CompletableFuture<>();

        future1.complete(sendResult);
        future2.complete(sendResult);
        future3.complete(sendResult);

        when(kafkaTemplate.send(anyString(), eq("101"), eq(event1))).thenReturn(future1);
        when(kafkaTemplate.send(anyString(), eq("102"), eq(event2))).thenReturn(future2);
        when(kafkaTemplate.send(anyString(), eq("103"), eq(event3))).thenReturn(future3);

        orderEventProducer.sendOrderCreatedEvent(event1);
        orderEventProducer.sendOrderCreatedEvent(event2);
        orderEventProducer.sendOrderCreatedEvent(event3);

        verify(kafkaTemplate).send(eq("order-events"), eq("101"), eq(event1));
        verify(kafkaTemplate).send(eq("order-events"), eq("102"), eq(event2));
        verify(kafkaTemplate).send(eq("order-events"), eq("103"), eq(event3));
    }

    @Test
    void sendOrderCreatedEvent_shouldHandleEventWithZeroAmount() {
        OrderCreatedEvent zeroAmountEvent = new OrderCreatedEvent(
                200L,
                300L,
                OrderStatus.PAYMENT_PENDING,
                BigDecimal.ZERO,
                new ArrayList<>()
        );

        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("order-events", 0),
                0L,
                0,
                System.currentTimeMillis(),
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "order-events",
                "200",
                zeroAmountEvent
        );
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, metadata);
        future.complete(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);

        orderEventProducer.sendOrderCreatedEvent(zeroAmountEvent);

        verify(kafkaTemplate).send(
                eq("order-events"),
                eq("200"),
                eventCaptor.capture()
        );

        assertThat(eventCaptor.getValue().getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void sendOrderCreatedEvent_shouldHandleEventWithLargeAmount() {
        OrderCreatedEvent largeAmountEvent = new OrderCreatedEvent(
                300L,
                400L,
                OrderStatus.CONFIRMED,
                new BigDecimal("999999.99"),
                createTestItems()
        );

        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("order-events", 0),
                0L,
                0,
                System.currentTimeMillis(),
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "order-events",
                "300",
                largeAmountEvent
        );
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, metadata);
        future.complete(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);

        orderEventProducer.sendOrderCreatedEvent(largeAmountEvent);

        verify(kafkaTemplate).send(
                eq("order-events"),
                eq("300"),
                eventCaptor.capture()
        );

        assertThat(eventCaptor.getValue().getTotalAmount())
                .isEqualByComparingTo(new BigDecimal("999999.99"));
    }

    @Test
    void sendOrderCreatedEvent_shouldHandleNullPointerExceptionInWhenComplete() {
        CompletableFuture<SendResult<String, Object>> nullFuture = new CompletableFuture<>();

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(nullFuture);

        orderEventProducer.sendOrderCreatedEvent(testEvent);

        nullFuture.completeExceptionally(new NullPointerException("Null result"));

        verify(kafkaTemplate).send(eq("order-events"), eq("100"), eq(testEvent));
    }

    @Test
    void sendOrderCreatedEvent_shouldNotThrowExceptionWhenKafkaFails() {
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Connection lost"));

        orderEventProducer.sendOrderCreatedEvent(testEvent);

        verify(kafkaTemplate).send(eq("order-events"), eq("100"), eq(testEvent));
    }

    @Test
    void sendOrderCreatedEvent_shouldHandleDifferentStatuses() {
        OrderCreatedEvent pendingEvent = new OrderCreatedEvent(
                400L,
                500L,
                OrderStatus.PAYMENT_PENDING,
                new BigDecimal("100.00"),
                createTestItems()
        );

        OrderCreatedEvent confirmedEvent = new OrderCreatedEvent(
                500L,
                600L,
                OrderStatus.CONFIRMED,
                new BigDecimal("200.00"),
                createTestItems()
        );

        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("order-events", 0),
                0L,
                0,
                System.currentTimeMillis(),
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "order-events",
                "400",
                pendingEvent
        );
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, metadata);

        CompletableFuture<SendResult<String, Object>> future1 = new CompletableFuture<>();
        CompletableFuture<SendResult<String, Object>> future2 = new CompletableFuture<>();

        future1.complete(sendResult);
        future2.complete(sendResult);

        when(kafkaTemplate.send(anyString(), eq("400"), eq(pendingEvent))).thenReturn(future1);
        when(kafkaTemplate.send(anyString(), eq("500"), eq(confirmedEvent))).thenReturn(future2);

        orderEventProducer.sendOrderCreatedEvent(pendingEvent);
        orderEventProducer.sendOrderCreatedEvent(confirmedEvent);

        verify(kafkaTemplate).send(eq("order-events"), eq("400"), eq(pendingEvent));
        verify(kafkaTemplate).send(eq("order-events"), eq("500"), eq(confirmedEvent));
    }

    @Test
    void sendOrderCreatedEvent_shouldHandleEventWithItems() {
        List<OrderItemEvent> items = createTestItems();
        OrderCreatedEvent eventWithItems = new OrderCreatedEvent(
                600L,
                700L,
                OrderStatus.PAYMENT_PENDING,
                new BigDecimal("300.00"),
                items
        );

        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("order-events", 0),
                0L,
                0,
                System.currentTimeMillis(),
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "order-events",
                "600",
                eventWithItems
        );
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, metadata);
        future.complete(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);

        orderEventProducer.sendOrderCreatedEvent(eventWithItems);

        verify(kafkaTemplate).send(
                eq("order-events"),
                eq("600"),
                eventCaptor.capture()
        );

        assertThat(eventCaptor.getValue().getItems()).hasSize(2);
    }

    private OrderCreatedEvent createEvent(Long orderId, OrderStatus status) {
        return new OrderCreatedEvent(
                orderId,
                orderId + 100,
                status,
                new BigDecimal("100.00"),
                createTestItems()
        );
    }

    private List<OrderItemEvent> createTestItems() {
        List<OrderItemEvent> items = new ArrayList<>();
        OrderItemEvent item1 = new OrderItemEvent();
        item1.setItemId(1L);
        item1.setItemName("Item 1");
        item1.setPrice(new BigDecimal("50.00"));
        item1.setQuantity(1);

        OrderItemEvent item2 = new OrderItemEvent();
        item2.setItemId(2L);
        item2.setItemName("Item 2");
        item2.setPrice(new BigDecimal("100.00"));
        item2.setQuantity(1);

        items.add(item1);
        items.add(item2);
        return items;
    }
}