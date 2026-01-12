package com.innowise.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testRecordValues() {
        ItemDto dto = new ItemDto(1L, "Laptop", new BigDecimal("1000.00"));

        assertEquals(1L, dto.id());
        assertEquals("Laptop", dto.name());
        assertEquals(new BigDecimal("1000.00"), dto.price());
    }

    @Test
    void testValidationConstraints() {
        ItemDto dto = new ItemDto(null, "", new BigDecimal("0.0"));

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }
}
