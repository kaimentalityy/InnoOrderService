package com.innowise.event;

import com.innowise.model.enums.EventType;
import com.innowise.model.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreatedEvent {

    private String eventId;
    private EventType eventType;
    private LocalDateTime eventTimestamp;

    private String paymentId;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private PaymentStatus status;

    public static PaymentCreatedEvent create(String paymentId,
                                             Long orderId,
                                             Long userId,
                                             BigDecimal amount,
                                             PaymentStatus status) {

        return PaymentCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(EventType.CREATE_PAYMENT)
                .eventTimestamp(LocalDateTime.now())
                .paymentId(paymentId)
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .status(status)
                .build();
    }
}
