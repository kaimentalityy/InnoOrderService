package com.innowise.orderservice.service.kafka;

import com.innowise.orderservice.dao.repository.OrderRepository;
import com.innowise.orderservice.event.PaymentCreatedEvent;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.model.entity.Order;

import com.innowise.orderservice.model.enums.OrderStatus;
import com.innowise.orderservice.model.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;

    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @KafkaListener(topics = "payment-events", groupId = "order-service")
    @Transactional
    public void handlePaymentCreatedEvent(PaymentCreatedEvent event) {
        log.info("Received payment event for order ID: {}, payment ID: {}, status: {}",
                event.getOrderId(), event.getPaymentId(), event.getStatus());

        String eventKey = event.getEventId();
        if (eventKey != null && !processedEvents.add(eventKey)) {
            log.info("Event {} already processed, skipping", eventKey);
            return;
        }

        try {
            updateOrderBasedOnPaymentStatus(event);
        } catch (OrderNotFoundException e) {
            log.error("Order {} not found for payment event", event.getOrderId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to process payment event for order {}", event.getOrderId(), e);
            if (eventKey != null) {
                processedEvents.remove(eventKey);
            }
            throw e;
        }
    }

    private void updateOrderBasedOnPaymentStatus(PaymentCreatedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException());

        OrderStatus orderStatus = mapPaymentStatusToOrderStatus(event.getStatus());

        log.info("Updating order {} from status {} to {}",
                order.getId(), order.getStatus(), orderStatus);

        order.setStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order {} status updated successfully to {}", order.getId(), orderStatus);
    }

    private OrderStatus mapPaymentStatusToOrderStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case SUCCESS -> OrderStatus.CONFIRMED;
            case FAILED -> OrderStatus.CANCELLED;
            case PENDING -> OrderStatus.PAYMENT_PENDING;
        };
    }
}
