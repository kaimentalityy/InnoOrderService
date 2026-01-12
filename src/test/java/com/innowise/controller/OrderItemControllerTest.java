package com.innowise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.model.dto.OrderItemDto;
import com.innowise.service.OrderItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderItemController.class)
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderItemService orderItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderItemDto orderItemDto;
    private static final String TEST_TOKEN = "Bearer test-token";
    private static final String RAW_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        orderItemDto = new OrderItemDto(1L, 10L, 20L, 5);
    }

    @Test
    void testCreate() throws Exception {
        given(orderItemService.create(any(OrderItemDto.class), eq(RAW_TOKEN))).willReturn(orderItemDto);

        mockMvc.perform(post("/api/order-items")
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.itemId").value(20))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(orderItemService).create(any(OrderItemDto.class), eq(RAW_TOKEN));
    }

    @Test
    void testUpdate() throws Exception {
        given(orderItemService.update(eq(1L), any(OrderItemDto.class), eq(RAW_TOKEN))).willReturn(orderItemDto);

        mockMvc.perform(put("/api/order-items/{id}", 1L)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.itemId").value(20))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(orderItemService).update(eq(1L), any(OrderItemDto.class), eq(RAW_TOKEN));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/order-items/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(orderItemService).delete(1L);
    }

    @Test
    void testGetById() throws Exception {
        given(orderItemService.findById(eq(1L), eq(RAW_TOKEN))).willReturn(orderItemDto);

        mockMvc.perform(get("/api/order-items/{id}", 1L)
                .header("Authorization", TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.itemId").value(20))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(orderItemService).findById(eq(1L), eq(RAW_TOKEN));
    }

    @Test
    void testSearch_withAllParams() throws Exception {
        Page<OrderItemDto> page = new PageImpl<>(List.of(orderItemDto));
        given(orderItemService.searchOrderItems(
                eq(10L), eq(20L), eq(5), eq(1), eq(10), eq(RAW_TOKEN), any(PageRequest.class))).willReturn(page);

        mockMvc.perform(get("/api/order-items")
                .header("Authorization", TEST_TOKEN)
                .param("orderId", "10")
                .param("itemId", "20")
                .param("quantity", "5")
                .param("minQuantity", "1")
                .param("maxQuantity", "10")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].orderId").value(10))
                .andExpect(jsonPath("$.content[0].itemId").value(20))
                .andExpect(jsonPath("$.content[0].quantity").value(5));

        verify(orderItemService).searchOrderItems(eq(10L), eq(20L), eq(5), eq(1), eq(10), eq(RAW_TOKEN),
                any(PageRequest.class));
    }

    @Test
    void testSearch_withNoParams() throws Exception {
        Page<OrderItemDto> page = new PageImpl<>(List.of(orderItemDto));
        given(orderItemService.searchOrderItems(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(RAW_TOKEN), any(PageRequest.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/order-items")
                .header("Authorization", TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].orderId").value(10))
                .andExpect(jsonPath("$.content[0].itemId").value(20))
                .andExpect(jsonPath("$.content[0].quantity").value(5));

        verify(orderItemService).searchOrderItems(isNull(), isNull(), isNull(), isNull(), isNull(), eq(RAW_TOKEN),
                any(PageRequest.class));
    }
}
