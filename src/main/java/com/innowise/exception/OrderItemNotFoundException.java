package com.innowise.exception;

import java.io.Serial;

/**
 * Thrown when an order item cannot be found.
 */
public class OrderItemNotFoundException extends OrderServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public OrderItemNotFoundException() {
        super(ErrorMessage.ORDER_ITEM_NOT_FOUND);
    }

    public OrderItemNotFoundException(Throwable cause) {
        super(ErrorMessage.ORDER_ITEM_NOT_FOUND, cause);
    }
}
