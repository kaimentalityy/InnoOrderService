package com.innowise.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionTest {


    @Test
    void orderServiceException_noArgConstructor_setsMessageAndErrorMessage() {
        OrderServiceException ex = new OrderServiceException(ErrorMessage.INTERNAL_ERROR);
        assertThat(ex.getMessage()).isEqualTo(ErrorMessage.INTERNAL_ERROR.getMessage());
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.INTERNAL_ERROR);
    }

    @Test
    void orderServiceException_withCause_setsMessageCauseAndErrorMessage() {
        RuntimeException cause = new RuntimeException("root cause");
        OrderServiceException ex = new OrderServiceException(ErrorMessage.INVALID_REQUEST, cause);
        assertThat(ex.getMessage()).isEqualTo(ErrorMessage.INVALID_REQUEST.getMessage());
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.INVALID_REQUEST);
    }


    @Test
    void orderNotFoundException_noArg() {
        OrderNotFoundException ex = new OrderNotFoundException();
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ORDER_NOT_FOUND);
        assertThat(ex.getMessage()).isEqualTo(ErrorMessage.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    void orderNotFoundException_withCause() {
        Throwable cause = new IllegalStateException("db error");
        OrderNotFoundException ex = new OrderNotFoundException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ORDER_NOT_FOUND);
    }


    @Test
    void itemNotFoundException_noArg() {
        ItemNotFoundException ex = new ItemNotFoundException();
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ITEM_NOT_FOUND);
    }

    @Test
    void itemNotFoundException_withCause() {
        Throwable cause = new RuntimeException("cause");
        ItemNotFoundException ex = new ItemNotFoundException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ITEM_NOT_FOUND);
    }


    @Test
    void orderItemNotFoundException_noArg() {
        OrderItemNotFoundException ex = new OrderItemNotFoundException();
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ORDER_ITEM_NOT_FOUND);
    }

    @Test
    void orderItemNotFoundException_withCause() {
        Throwable cause = new RuntimeException("cause");
        OrderItemNotFoundException ex = new OrderItemNotFoundException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ORDER_ITEM_NOT_FOUND);
    }


    @Test
    void entityAlreadyExistsException_noArg() {
        EntityAlreadyExistsException ex = new EntityAlreadyExistsException();
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ENTITY_ALREADY_EXISTS);
    }

    @Test
    void entityAlreadyExistsException_withCause() {
        Throwable cause = new RuntimeException("dup key");
        EntityAlreadyExistsException ex = new EntityAlreadyExistsException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ENTITY_ALREADY_EXISTS);
    }


    @Test
    void orderConflictException_noArg() {
        OrderConflictException ex = new OrderConflictException();
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ORDER_CONFLICT);
    }

    @Test
    void orderConflictException_withCause() {
        Throwable cause = new RuntimeException("conflict");
        OrderConflictException ex = new OrderConflictException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.ORDER_CONFLICT);
    }


    @Test
    void paymentFailedException_noArg() {
        PaymentFailedException ex = new PaymentFailedException();
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.PAYMENT_FAILED);
    }

    @Test
    void paymentFailedException_withCause() {
        Throwable cause = new RuntimeException("payment error");
        PaymentFailedException ex = new PaymentFailedException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getErrorMessage()).isEqualTo(ErrorMessage.PAYMENT_FAILED);
    }
}
