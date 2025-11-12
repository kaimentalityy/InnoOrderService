package com.innowise.orderservice.service.kafka;

import com.innowise.orderservice.event.PaymentCreatedEvent;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.dto.OrderDto;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderServiceImpl orderService;
    private final OrderMapper orderMapper;

    @KafkaListener(topics = "payment-events", groupId = "order-service")
    public void handlePaymentCreatedEvent(PaymentCreatedEvent event) {
        if ("CREATE_PAYMENT".equals(event.getEventType())) {
            log.info("Received CREATE_PAYMENT event for order ID: {}", event.getOrderId());
            updateOrderStatus(event);
        }
    }
    
    private void updateOrderStatus(PaymentCreatedEvent event) {
        try {
            log.info("Updating order status for order ID: {} based on payment status: {}", 
                    event.getOrderId(), event.getStatus());

            OrderDto orderDto = orderService.findById(event.getOrderId());
            Order order = orderMapper.toEntity(orderDto);

            order.setStatus(event.getStatus());

            orderService.create(orderMapper.toDto(order));

            log.info("Order status updated successfully for order ID: {}", event.getOrderId());
            
        } catch (Exception e) {
            log.error("Failed to update order status for order ID: {}", event.getOrderId(), e);
        }
    }
}