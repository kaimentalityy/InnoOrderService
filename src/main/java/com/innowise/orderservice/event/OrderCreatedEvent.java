package com.innowise.orderservice.event;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderCreatedEvent {
    private String eventId;
    private String eventType = "ORDER_CREATED";
    private LocalDateTime eventTimestamp;
    private Long orderId;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemEvent> items;
    
    public OrderCreatedEvent() {
        this.eventTimestamp = LocalDateTime.now();
        this.eventId = java.util.UUID.randomUUID().toString();
    }
    
    public OrderCreatedEvent(Long orderId, Long userId, String status, 
                           BigDecimal totalAmount, List<OrderItemEvent> items) {
        this();
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
    }
}