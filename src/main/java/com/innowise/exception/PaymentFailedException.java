package com.innowise.exception;

import java.io.Serial;

/**
 * Thrown when a payment operation fails.
 */
public class PaymentFailedException extends OrderServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PaymentFailedException() {
        super(ErrorMessage.PAYMENT_FAILED);
    }

    public PaymentFailedException(Throwable cause) {
        super(ErrorMessage.PAYMENT_FAILED, cause);
    }
}
