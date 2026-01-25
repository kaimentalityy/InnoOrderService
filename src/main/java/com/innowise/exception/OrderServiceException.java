package com.innowise.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * Base class for all custom exceptions in the Order Service.
 */
@Getter
public class OrderServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ErrorMessage errorMessage;

    public OrderServiceException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public OrderServiceException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getMessage(), cause);
        this.errorMessage = errorMessage;
    }
}
