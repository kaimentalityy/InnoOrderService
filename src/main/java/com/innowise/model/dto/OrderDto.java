package com.innowise.model.dto;

import com.innowise.model.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,

        @NotNull(message = "User ID cannot be null")
        Long userId,

        @NotNull
        OrderStatus status,

        @PastOrPresent(message = "Creation date must be in the present or past")
        LocalDateTime createdDate,

        @Valid
        List<OrderItemDto> items,

        UserInfoDto userInfo
) {}


