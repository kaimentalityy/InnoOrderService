package com.innowise.exception;

import com.innowise.model.dto.ErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleOrderNotFound_ShouldReturn404() {
        OrderNotFoundException ex = new OrderNotFoundException();
        var response = handler.handleOrderNotFound(ex);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        ErrorDto body = response.getBody();
        assertNotNull(body);
        assertTrue(body.message().contains(ErrorMessage.ORDER_NOT_FOUND.getMessage()));
        assertEquals(404, body.value());
    }

    @Test
    void handleEntityAlreadyExists_ShouldReturn409() {
        EntityAlreadyExistsException ex = new EntityAlreadyExistsException();
        var response = handler.handleEntityAlreadyExists(ex);

        assertNotNull(response);
        assertEquals(409, response.getStatusCodeValue());
        ErrorDto body = response.getBody();
        assertNotNull(body);
        assertTrue(body.message().contains(ErrorMessage.ENTITY_ALREADY_EXISTS.getMessage()));
        assertEquals(409, body.value());
    }

    @Test
    void handleOrderConflict_ShouldReturn409() {
        OrderConflictException ex = new OrderConflictException();
        var response = handler.handleOrderConflict(ex);

        assertNotNull(response);
        assertEquals(409, response.getStatusCodeValue());
        ErrorDto body = response.getBody();
        assertNotNull(body);
        assertTrue(body.message().contains(ErrorMessage.ORDER_CONFLICT.getMessage()));
        assertEquals(409, body.value());
    }

    @Test
    void handleOrderItemNotFound_ShouldReturn404() {
        OrderItemNotFoundException ex = new OrderItemNotFoundException();
        var response = handler.handleOrderItemNotFound(ex);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        ErrorDto body = response.getBody();
        assertNotNull(body);
        assertTrue(body.message().contains(ErrorMessage.ORDER_ITEM_NOT_FOUND.getMessage()));
        assertEquals(404, body.value());
    }

    @Test
    void handlePaymentFailed_ShouldReturn400() {
        PaymentFailedException ex = new PaymentFailedException();
        var response = handler.handlePaymentFailed(ex);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        ErrorDto body = response.getBody();
        assertNotNull(body);
        assertTrue(body.message().contains(ErrorMessage.PAYMENT_FAILED.getMessage()));
        assertEquals(400, body.value());
    }

    @Test
    void handleGenericOrderException_ShouldReturn500() {
        OrderServiceException ex = new OrderServiceException(ErrorMessage.INTERNAL_ERROR);
        var response = handler.handleGenericOrderException(ex);

        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        ErrorDto body = response.getBody();
        assertNotNull(body);
        assertTrue(body.message().contains(ErrorMessage.INTERNAL_ERROR.getMessage()));
        assertEquals(500, body.value());
    }

    @Test
    void handleUnhandled_ShouldReturn500WithInternalError() {
        RuntimeException ex = new RuntimeException("some unexpected");
        var response = handler.handleUnhandled(ex);

        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        ErrorDto body = response.getBody();
        assertNotNull(body);
        assertTrue(body.message().contains(ErrorMessage.INTERNAL_ERROR.getMessage()));
        assertEquals(500, body.value());
    }

    @Test
    void handleValidationErrors_ShouldReturnBadRequestAndCombinedMessages() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError f1 = new FieldError("object", "field1", "must not be blank");
        FieldError f2 = new FieldError("object", "field2", "must be positive");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(f1, f2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ErrorDto errorDto = handler.handleValidationErrors(ex);

        assertNotNull(errorDto);
        String message = errorDto.message();
        assertTrue(message.contains("field1: must not be blank"));
        assertTrue(message.contains("field2: must be positive"));
        assertEquals(400, errorDto.value());
    }
}
