package com.innowise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.config.TestSecurityConfig;
import com.innowise.model.dto.OrderItemDto;
import com.innowise.service.OrderItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderItemController.class, excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class,
                org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
                org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
                org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
                org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class
})
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@WithMockUser
class OrderItemControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderItemService orderItemService;

        @Autowired
        private ObjectMapper objectMapper;

        private OrderItemDto orderItemDto;

        @BeforeEach
        void setUp() {
                orderItemDto = new OrderItemDto(1L, 10L, 20L, 5);
        }

        @Test
        void testCreate() throws Exception {
                given(orderItemService.create(any(OrderItemDto.class))).willReturn(orderItemDto);

                mockMvc.perform(post("/api/order-items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderItemDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.orderId").value(10))
                                .andExpect(jsonPath("$.itemId").value(20))
                                .andExpect(jsonPath("$.quantity").value(5));

                verify(orderItemService).create(any(OrderItemDto.class));
        }

        @Test
        void testUpdate() throws Exception {
                given(orderItemService.update(eq(1L), any(OrderItemDto.class))).willReturn(orderItemDto);

                mockMvc.perform(put("/api/order-items/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderItemDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.orderId").value(10))
                                .andExpect(jsonPath("$.itemId").value(20))
                                .andExpect(jsonPath("$.quantity").value(5));

                verify(orderItemService).update(eq(1L), any(OrderItemDto.class));
        }

        @Test
        void testDelete() throws Exception {
                mockMvc.perform(delete("/api/order-items/{id}", 1L))
                                .andExpect(status().isNoContent());

                verify(orderItemService).delete(1L);
        }

        @Test
        void testGetById() throws Exception {
                given(orderItemService.findById(eq(1L))).willReturn(orderItemDto);

                mockMvc.perform(get("/api/order-items/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.orderId").value(10))
                                .andExpect(jsonPath("$.itemId").value(20))
                                .andExpect(jsonPath("$.quantity").value(5));

                verify(orderItemService).findById(eq(1L));
        }

        @Test
        void testSearch_withAllParams() throws Exception {
                Page<OrderItemDto> page = new PageImpl<>(List.of(orderItemDto));
                given(orderItemService.searchOrderItems(
                                eq(10L), eq(20L), eq(5), eq(1), eq(10), any(PageRequest.class))).willReturn(page);

                mockMvc.perform(get("/api/order-items")
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

                verify(orderItemService).searchOrderItems(eq(10L), eq(20L), eq(5), eq(1), eq(10),
                                any(PageRequest.class));
        }

        @Test
        void testSearch_withNoParams() throws Exception {
                Page<OrderItemDto> page = new PageImpl<>(List.of(orderItemDto));
                given(orderItemService.searchOrderItems(
                                isNull(), isNull(), isNull(), isNull(), isNull(), any(PageRequest.class)))
                                .willReturn(page);

                mockMvc.perform(get("/api/order-items"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].orderId").value(10))
                                .andExpect(jsonPath("$.content[0].itemId").value(20))
                                .andExpect(jsonPath("$.content[0].quantity").value(5));

                verify(orderItemService).searchOrderItems(isNull(), isNull(), isNull(), isNull(), isNull(),
                                any(PageRequest.class));
        }
}
