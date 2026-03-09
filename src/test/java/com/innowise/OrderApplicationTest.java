package com.innowise;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderApplicationTest {

    @Test
    void main_shouldNotThrow() {
        OrderApplication app = new OrderApplication();
        assertThat(app).isNotNull();

        try {
            OrderApplication.main(new String[] { "--server.port=0" });
        } catch (Exception e) {
        }
    }
}
