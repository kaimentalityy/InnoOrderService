package com.innowise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.model.dto.OrderDto;
import com.innowise.model.enums.OrderStatus;
import com.innowise.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;

        @Autowired
        private ObjectMapper objectMapper;

        private OrderDto orderDto;
        private static final String TEST_TOKEN = "Bearer test-token";
        private static final String RAW_TOKEN = "test-token";

        @BeforeEach
        void setUp() {
                orderDto = new OrderDto(
                                1L,
                                123L,
                                OrderStatus.PAYMENT_PENDING,
                                LocalDateTime.of(2024, 10, 10, 12, 0),
                                List.of(),
                                null);
        }

        @Test
        void testCreate() throws Exception {
                given(orderService.create(any(OrderDto.class), eq(RAW_TOKEN))).willReturn(orderDto);

                mockMvc.perform(post("/api/orders")
                                .header("Authorization", TEST_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.status").value("PAYMENT_PENDING"))
                                .andExpect(jsonPath("$.userId").value("123"));

                verify(orderService).create(any(OrderDto.class), eq(RAW_TOKEN));
        }

        @Test
        void testUpdate() throws Exception {
                given(orderService.update(eq(1L), any(OrderDto.class), eq(RAW_TOKEN))).willReturn(orderDto);

                mockMvc.perform(put("/api/orders/{id}", 1L)
                                .header("Authorization", TEST_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.status").value("PAYMENT_PENDING"));

                verify(orderService).update(eq(1L), any(OrderDto.class), eq(RAW_TOKEN));
        }

        @Test
        void testDelete() throws Exception {
                mockMvc.perform(delete("/api/orders/{id}", 1L))
                                .andExpect(status().isNoContent());

                verify(orderService).delete(1L);
        }

        @Test
        void testGetById() throws Exception {
                given(orderService.findById(eq(1L), eq(RAW_TOKEN))).willReturn(orderDto);

                mockMvc.perform(get("/api/orders/{id}", 1L)
                                .header("Authorization", TEST_TOKEN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.status").value("PAYMENT_PENDING"));

                verify(orderService).findById(eq(1L), eq(RAW_TOKEN));
        }

        @Test
        void testSearch_withAllParams() throws Exception {
                Page<OrderDto> page = new PageImpl<>(List.of(orderDto));

                given(orderService.searchOrders(
                                eq(123L), eq("user@mail.com"), eq("PENDING"),
                                any(LocalDateTime.class), any(LocalDateTime.class),
                                eq(RAW_TOKEN),
                                any(Pageable.class))).willReturn(page);

                mockMvc.perform(get("/api/orders")
                                .header("Authorization", TEST_TOKEN)
                                .param("userId", "123")
                                .param("email", "user@mail.com")
                                .param("status", "PENDING")
                                .param("createdAfter", "2023-01-01T00:00:00")
                                .param("createdBefore", "2025-01-01T00:00:00")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].status").value("PAYMENT_PENDING"));

                verify(orderService).searchOrders(
                                eq(123L), eq("user@mail.com"), eq("PENDING"),
                                any(LocalDateTime.class), any(LocalDateTime.class),
                                eq(RAW_TOKEN),
                                any(Pageable.class));
        }

        @Test
        void testSearch_withNoParams() throws Exception {
                Page<OrderDto> page = new PageImpl<>(List.of(orderDto));
                given(orderService.searchOrders(
                                isNull(), isNull(), isNull(), isNull(), isNull(), eq(RAW_TOKEN), any(Pageable.class)))
                                .willReturn(page);

                mockMvc.perform(get("/api/orders")
                                .header("Authorization", TEST_TOKEN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].status").value("PAYMENT_PENDING"));

                verify(orderService).searchOrders(
                                isNull(), isNull(), isNull(), isNull(), isNull(), eq(RAW_TOKEN), any(Pageable.class));
        }
}
