package com.innowise.exception;

import com.innowise.model.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

/**
 * Global exception handler for the Order Service.
 * It captures all application-specific and runtime exceptions,
 * returning structured JSON responses.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorDto> buildResponse(HttpStatus status, ErrorMessage errorMessage, Throwable ex) {
        log.error("Exception caught: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        String message = String.format("[%s] %s — %s",
                LocalDateTime.now(),
                errorMessage.getMessage(),
                ex.getClass().getSimpleName()
        );

        return ResponseEntity
                .status(status)
                .body(new ErrorDto(message, status.value()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorDto> handleOrderNotFound(OrderNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getErrorMessage(), ex);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleEntityAlreadyExists(EntityAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getErrorMessage(), ex);
    }

    @ExceptionHandler(OrderConflictException.class)
    public ResponseEntity<ErrorDto> handleOrderConflict(OrderConflictException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getErrorMessage(), ex);
    }

    @ExceptionHandler(OrderItemNotFoundException.class)
    public ResponseEntity<ErrorDto> handleOrderItemNotFound(OrderItemNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getErrorMessage(), ex);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorDto> handlePaymentFailed(PaymentFailedException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getErrorMessage(), ex);
    }

    @ExceptionHandler(OrderServiceException.class)
    public ResponseEntity<ErrorDto> handleGenericOrderException(OrderServiceException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getErrorMessage(), ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnhandled(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_ERROR, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Validation failed");

        log.warn("Validation error: {}", message);
        return new ErrorDto(message, HttpStatus.BAD_REQUEST.value());
    }
}
