package com.innowise.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testRecordValues() {
        OrderItemDto dto = new OrderItemDto(1L, 2L, 3L, 5);

        assertEquals(1L, dto.id());
        assertEquals(2L, dto.orderId());
        assertEquals(3L, dto.itemId());
        assertEquals(5, dto.quantity());
    }

    @Test
    void testValidationConstraints() {
        OrderItemDto dto = new OrderItemDto(
                null,
                null,
                null,
                0
        );

        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }
}
