package com.innowise.exception;

import java.io.Serial;

public class ItemNotFoundException extends OrderServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ItemNotFoundException() {
        super(ErrorMessage.ITEM_NOT_FOUND);
    }

    public ItemNotFoundException(Throwable cause) {
        super(ErrorMessage.ITEM_NOT_FOUND, cause);
    }
}
