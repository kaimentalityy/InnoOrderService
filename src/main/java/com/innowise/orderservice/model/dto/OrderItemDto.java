package com.innowise.orderservice.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemDto(
        Long id,

        @NotNull(message = "Order ID cannot be null")
        Long orderId,

        @NotNull(message = "Item ID cannot be null")
        Long itemId,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}
