package com.innowise.model.dto;

import com.innowise.model.enums.OrderStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testRecordValues() {
        OrderItemDto itemDto = new OrderItemDto(10L, 1L, 20L, 2);

        OrderDto dto = new OrderDto(
                1L,
                123L,
                OrderStatus.CONFIRMED,
                LocalDateTime.now(),
                List.of(itemDto),
                null
        );


        assertEquals(1L, dto.id());
        assertEquals(123L, dto.userId());
        assertEquals("CONFIRMED", dto.status().name());
        assertEquals(1, dto.items().size());
        assertEquals(10L, dto.items().getFirst().id());
    }

    @Test
    void testValidationConstraints() {
        OrderItemDto itemDto = new OrderItemDto(10L, 1L, 20L, 2);

        OrderDto dto = new OrderDto(
                1L,
                null,
                null,
                LocalDateTime.now().plusDays(1),
                List.of(itemDto),
                null
        );

        Set<ConstraintViolation<OrderDto>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }

}
