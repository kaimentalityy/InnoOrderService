package com.innowise.orderservice.service.kafka;

import com.innowise.orderservice.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventProducer {
    
    private static final String ORDER_EVENTS_TOPIC = "order-events";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
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
