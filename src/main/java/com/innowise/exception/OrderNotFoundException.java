package com.innowise.exception;

import java.io.Serial;

/**
 * Thrown when an order with the given identifier cannot be found.
 */
public class OrderNotFoundException extends OrderServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public OrderNotFoundException() {
        super(ErrorMessage.ORDER_NOT_FOUND);
    }

    public OrderNotFoundException(Throwable cause) {
        super(ErrorMessage.ORDER_NOT_FOUND, cause);
    }
}
