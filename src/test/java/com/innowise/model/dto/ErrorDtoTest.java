package com.innowise.model.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorDtoTest {

    @Test
    void errorDto_recordAccessors() {
        ErrorDto dto = new ErrorDto("something went wrong", 500);
        assertThat(dto.message()).isEqualTo("something went wrong");
        assertThat(dto.value()).isEqualTo(500);
    }

    @Test
    void errorDto_equals_hashCode() {
        ErrorDto dto1 = new ErrorDto("msg", 404);
        ErrorDto dto2 = new ErrorDto("msg", 404);
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void errorDto_notEqual_differentValues() {
        ErrorDto dto1 = new ErrorDto("msg", 404);
        ErrorDto dto2 = new ErrorDto("other", 500);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void errorDto_toString() {
        ErrorDto dto = new ErrorDto("error", 400);
        assertThat(dto.toString()).contains("error");
        assertThat(dto.toString()).contains("400");
    }
}
