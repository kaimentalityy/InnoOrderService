package com.innowise.service.kafka;

import com.innowise.dao.repository.OrderRepository;
import com.innowise.event.PaymentCreatedEvent;
import com.innowise.exception.OrderNotFoundException;
import com.innowise.model.entity.Order;
import com.innowise.model.enums.OrderStatus;
import com.innowise.model.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Kafka consumer for handling payment events.
 * Listens to payment topics and updates order status based on payment outcomes.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;

    /**
     * Handles payment created events.
     * Validates the event and updates the order status accordingly.
     *
     * @param topic     the topic from which the event was received
     * @param key       the message key
     * @param partition the partition from which the event was received
     * @param offset    the offset of the message
     * @param event     the payment created event
     */
    @KafkaListener(topics = "${spring.kafka.topic.payment-events}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handlePaymentCreatedEvent(
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            PaymentCreatedEvent event) {

        log.info(
                "Received payment event - topic: {}, partition: {}, offset: {}, key: {}, orderId: {}, paymentId: {}, status: {}",
                topic, partition, offset, key, event.getOrderId(), event.getPaymentId(), event.getStatus());

        validateEvent(event);
        updateOrderBasedOnPaymentStatus(event);
    }

    private void validateEvent(PaymentCreatedEvent event) {
        Assert.notNull(event, "Payment event cannot be null");
        Assert.notNull(event.getOrderId(), "Order ID cannot be null");
        Assert.notNull(event.getPaymentId(), "Payment ID cannot be null");
        Assert.notNull(event.getStatus(), "Payment status cannot be null");
    }

    private void updateOrderBasedOnPaymentStatus(PaymentCreatedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException());

        OrderStatus newStatus = mapPaymentStatusToOrderStatus(event.getStatus());
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == newStatus) {
            log.debug("Order {} already in status {}", order.getId(), currentStatus);
            return;
        }

        if (isFinalState(currentStatus) && newStatus == OrderStatus.PAYMENT_PENDING) {
            log.warn("Ignoring out-of-order payment event. Order {} is already in final state {}. Event status: {}",
                    order.getId(), currentStatus, newStatus);
            return;
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
        log.info("Order {} status updated successfully to {}", order.getId(), newStatus);
    }

    private boolean isFinalState(OrderStatus status) {
        return status == OrderStatus.CONFIRMED || status == OrderStatus.CANCELLED;
    }

    private OrderStatus mapPaymentStatusToOrderStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case SUCCESS -> OrderStatus.CONFIRMED;
            case FAILED -> OrderStatus.CANCELLED;
            case PENDING -> OrderStatus.PAYMENT_PENDING;
        };
    }

    /**
     * Handles payment events from the Dead Letter Topic (DLT).
     * Logs the failed event for manual inspection.
     *
     * @param topic     the topic from which the event was received
     * @param key       the message key
     * @param partition the partition from which the event was received
     * @param offset    the offset of the message
     * @param event     the payment created event
     */
    @KafkaListener(topics = "${spring.kafka.topic.payment-events}.DLT", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentEventsDlt(
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            PaymentCreatedEvent event) {

        log.error(
                "DLT - Received failed payment event - topic: {}, partition: {}, offset: {}, key: {}, orderId: {}, paymentId: {}, status: {}",
                topic, partition, offset, key, event.getOrderId(), event.getPaymentId(), event.getStatus());
    }
}
