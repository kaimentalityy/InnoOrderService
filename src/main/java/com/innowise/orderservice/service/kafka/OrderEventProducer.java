package com.innowise.orderservice.service.kafka;

import com.innowise.orderservice.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer for sending order-related events to Kafka.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventProducer {

    @Value("${spring.kafka.topic.order-events:order-events}")
    private String ORDER_EVENTS_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends an OrderCreatedEvent to the configured Kafka topic.
     *
     * @param event the event to send
     */
    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            log.info("Sending ORDER_CREATED event for order ID: {}", event.getOrderId());

            kafkaTemplate.send(ORDER_EVENTS_TOPIC, event.getOrderId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("ORDER_CREATED event sent successfully for order ID: {}, offset: {}",
                                    event.getOrderId(), result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send ORDER_CREATED event for order ID: {}",
                                    event.getOrderId(), ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Error sending ORDER_CREATED event for order ID: {}", event.getOrderId(), e);
        }
    }
}
