package com.innowise.exception;

import java.io.Serial;

/**
 * Thrown when a conflict occurs while updating or processing an order.
 */
public class OrderConflictException extends OrderServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public OrderConflictException() {
        super(ErrorMessage.ORDER_CONFLICT);
    }

    public OrderConflictException(Throwable cause) {
        super(ErrorMessage.ORDER_CONFLICT, cause);
    }
}
