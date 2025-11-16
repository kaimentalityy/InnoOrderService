package com.innowise.orderservice.event;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {
    private Long itemId;
    private String itemName;
    private BigDecimal price;
    private Integer quantity;
}
