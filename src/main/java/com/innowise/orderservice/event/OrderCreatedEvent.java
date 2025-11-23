package com.innowise.orderservice.event;

import com.innowise.orderservice.model.enums.EventType;
import com.innowise.orderservice.model.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderCreatedEvent {
    private String eventId = java.util.UUID.randomUUID().toString();
    private EventType eventType = EventType.ORDER_CREATE;
    private LocalDateTime eventTimestamp = LocalDateTime.now();
    private Long orderId;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemEvent> items;

    public OrderCreatedEvent(Long orderId, Long userId, OrderStatus status,
            BigDecimal totalAmount, List<OrderItemEvent> items) {

        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
    }
}
