package com.innowise.orderservice.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentCreatedEvent {
    private String eventId;
    private String eventType;
    private LocalDateTime eventTimestamp;
    private Long paymentId;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String status;
}
