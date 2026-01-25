package com.innowise.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserInfoDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testRecordAccessors() {
        UserInfoDto dto = new UserInfoDto(1L, "John", "Doe", "john.doe@example.com");

        assertEquals(1L, dto.id());
        assertEquals("John", dto.name());
        assertEquals("Doe", dto.surname());
        assertEquals("john.doe@example.com", dto.email());
    }

    @Test
    void testValidationConstraints() {
        UserInfoDto dto = new UserInfoDto(null, "", "", "invalid-email");

        Set<ConstraintViolation<UserInfoDto>> violations = validator.validate(dto);
        assertEquals(6, violations.size());
    }
}
