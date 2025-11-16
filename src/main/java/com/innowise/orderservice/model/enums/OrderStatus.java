package com.innowise.orderservice.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PAYMENT_PENDING("PAYMENT_PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED"),;

    private final String value;
}
