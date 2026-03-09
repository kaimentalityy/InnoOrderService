package com.innowise.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorMessageTest {

    @Test
    void allEnumValues_haveNonBlankMessages() {
        for (ErrorMessage em : ErrorMessage.values()) {
            assertThat(em.getMessage()).isNotBlank();
        }
    }

    @Test
    void valueOf_returnsCorrectConstants() {
        assertThat(ErrorMessage.valueOf("INTERNAL_ERROR")).isEqualTo(ErrorMessage.INTERNAL_ERROR);
        assertThat(ErrorMessage.valueOf("INVALID_REQUEST")).isEqualTo(ErrorMessage.INVALID_REQUEST);
        assertThat(ErrorMessage.valueOf("ORDER_NOT_FOUND")).isEqualTo(ErrorMessage.ORDER_NOT_FOUND);
        assertThat(ErrorMessage.valueOf("ENTITY_ALREADY_EXISTS")).isEqualTo(ErrorMessage.ENTITY_ALREADY_EXISTS);
        assertThat(ErrorMessage.valueOf("ORDER_CONFLICT")).isEqualTo(ErrorMessage.ORDER_CONFLICT);
        assertThat(ErrorMessage.valueOf("ORDER_ITEM_NOT_FOUND")).isEqualTo(ErrorMessage.ORDER_ITEM_NOT_FOUND);
        assertThat(ErrorMessage.valueOf("PAYMENT_FAILED")).isEqualTo(ErrorMessage.PAYMENT_FAILED);
        assertThat(ErrorMessage.valueOf("ITEM_NOT_FOUND")).isEqualTo(ErrorMessage.ITEM_NOT_FOUND);
    }

    @Test
    void values_returnsAllConstants() {
        ErrorMessage[] values = ErrorMessage.values();
        assertThat(values).hasSize(8);
    }

    @Test
    void specificMessages_areCorrect() {
        assertThat(ErrorMessage.INTERNAL_ERROR.getMessage()).isEqualTo("An unexpected internal error occurred");
        assertThat(ErrorMessage.ORDER_NOT_FOUND.getMessage()).isEqualTo("Order not found");
        assertThat(ErrorMessage.ITEM_NOT_FOUND.getMessage()).isEqualTo("Item not found");
        assertThat(ErrorMessage.ORDER_ITEM_NOT_FOUND.getMessage()).isEqualTo("Order item not found");
        assertThat(ErrorMessage.ENTITY_ALREADY_EXISTS.getMessage()).isEqualTo("Entity already exists");
        assertThat(ErrorMessage.ORDER_CONFLICT.getMessage()).isEqualTo("Order conflict detected");
        assertThat(ErrorMessage.PAYMENT_FAILED.getMessage()).isEqualTo("Payment processing failed");
        assertThat(ErrorMessage.INVALID_REQUEST.getMessage()).isEqualTo("Invalid request parameters");
    }
}
