package com.innowise.exception;

import java.io.Serial;

/**
 * Thrown when trying to create an order that already exists.
 */
public class EntityAlreadyExistsException extends OrderServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EntityAlreadyExistsException() {
        super(ErrorMessage.ENTITY_ALREADY_EXISTS);
    }

    public EntityAlreadyExistsException(Throwable cause) {
        super(ErrorMessage.ENTITY_ALREADY_EXISTS, cause);
    }
}
