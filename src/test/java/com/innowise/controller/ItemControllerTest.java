package com.innowise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.model.dto.ItemDto;
import com.innowise.service.ItemService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private static final String TEST_TOKEN = "Bearer test-token";
    private static final String RAW_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Laptop", BigDecimal.valueOf(999.99));
    }

    @Test
    void testCreate() throws Exception {
        given(itemService.create(any(ItemDto.class), eq(RAW_TOKEN))).willReturn(itemDto);

        mockMvc.perform(post("/api/items")
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99));

        verify(itemService).create(any(ItemDto.class), eq(RAW_TOKEN));
    }

    @Test
    void testUpdate() throws Exception {
        given(itemService.update(eq(1L), any(ItemDto.class), eq(RAW_TOKEN))).willReturn(itemDto);

        mockMvc.perform(put("/api/items/{id}", 1)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(itemService).update(eq(1L), any(ItemDto.class), eq(RAW_TOKEN));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/items/{id}", 1))
                .andExpect(status().isNoContent());

        verify(itemService).delete(1L);
    }

    @Test
    void testGetById() throws Exception {
        given(itemService.findById(eq(1L), eq(RAW_TOKEN))).willReturn(itemDto);

        mockMvc.perform(get("/api/items/{id}", 1)
                .header("Authorization", TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(itemService).findById(eq(1L), eq(RAW_TOKEN));
    }

    @Test
    void testSearch_withAllParams() throws Exception {
        Page<ItemDto> page = new PageImpl<>(List.of(itemDto));
        given(itemService.searchItems(eq("Laptop"), eq("1000"), eq("ExactName"), eq(RAW_TOKEN), any(PageRequest.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/items")
                .header("Authorization", TEST_TOKEN)
                .param("name", "Laptop")
                .param("price", "1000")
                .param("exactName", "ExactName")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));

        verify(itemService).searchItems(eq("Laptop"), eq("1000"), eq("ExactName"), eq(RAW_TOKEN),
                any(PageRequest.class));
    }

    @Test
    void testSearch_withNoParams() throws Exception {
        Page<ItemDto> page = new PageImpl<>(List.of(itemDto));
        given(itemService.searchItems(isNull(), isNull(), isNull(), eq(RAW_TOKEN), any(PageRequest.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/items")
                .header("Authorization", TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));

        verify(itemService).searchItems(isNull(), isNull(), isNull(), eq(RAW_TOKEN), any(PageRequest.class));
    }
}
