package com.innowise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.config.TestSecurityConfig;
import com.innowise.model.dto.ItemDto;
import com.innowise.service.ItemService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class, excludeAutoConfiguration = {
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
class ItemControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ItemService itemService;

        @Autowired
        private ObjectMapper objectMapper;

        private ItemDto itemDto;

        @BeforeEach
        void setUp() {
                itemDto = new ItemDto(1L, "Laptop", BigDecimal.valueOf(999.99));
        }

        @Test
        void testCreate() throws Exception {
                given(itemService.create(any(ItemDto.class))).willReturn(itemDto);

                mockMvc.perform(post("/api/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Laptop"))
                                .andExpect(jsonPath("$.price").value(999.99));

                verify(itemService).create(any(ItemDto.class));
        }

        @Test
        void testUpdate() throws Exception {
                given(itemService.update(eq(1L), any(ItemDto.class))).willReturn(itemDto);

                mockMvc.perform(put("/api/items/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Laptop"));

                verify(itemService).update(eq(1L), any(ItemDto.class));
        }

        @Test
        void testDelete() throws Exception {
                mockMvc.perform(delete("/api/items/{id}", 1))
                                .andExpect(status().isNoContent());

                verify(itemService).delete(1L);
        }

        @Test
        void testGetById() throws Exception {
                given(itemService.findById(eq(1L))).willReturn(itemDto);

                mockMvc.perform(get("/api/items/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Laptop"));

                verify(itemService).findById(eq(1L));
        }

        @Test
        void testSearch_withAllParams() throws Exception {
                Page<ItemDto> page = new PageImpl<>(List.of(itemDto));
                given(itemService.searchItems(eq("Laptop"), eq("1000"), eq("ExactName"), any(PageRequest.class)))
                                .willReturn(page);

                mockMvc.perform(get("/api/items")
                                .param("name", "Laptop")
                                .param("price", "1000")
                                .param("exactName", "ExactName")
                                .param("page", "0")
                                .param("size", "5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].name").value("Laptop"));

                verify(itemService).searchItems(eq("Laptop"), eq("1000"), eq("ExactName"),
                                any(PageRequest.class));
        }

        @Test
        void testSearch_withNoParams() throws Exception {
                Page<ItemDto> page = new PageImpl<>(List.of(itemDto));
                given(itemService.searchItems(isNull(), isNull(), isNull(), any(PageRequest.class)))
                                .willReturn(page);

                mockMvc.perform(get("/api/items"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].name").value("Laptop"));

                verify(itemService).searchItems(isNull(), isNull(), isNull(), any(PageRequest.class));
        }
}
