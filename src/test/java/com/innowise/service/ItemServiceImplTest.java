package com.innowise.service;

import com.innowise.dao.repository.ItemRepository;
import com.innowise.exception.ItemNotFoundException;
import com.innowise.mapper.ItemMapper;
import com.innowise.model.dto.ItemDto;
import com.innowise.model.entity.Item;
import com.innowise.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item entity;
    private ItemDto dto;
    private static final String TEST_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        entity = new Item();
        entity.setId(1L);
        entity.setName("Pencil");
        entity.setPrice(BigDecimal.valueOf(10.0));

        dto = new ItemDto(
                1L,
                "Pencil",
                BigDecimal.valueOf(10.0));
    }

    @Test
    void create_ShouldSaveAndReturnDto() {
        when(itemMapper.toEntity(dto)).thenReturn(entity);
        when(itemRepository.save(entity)).thenReturn(entity);
        when(itemMapper.toDto(entity)).thenReturn(dto);

        ItemDto result = itemService.create(dto, TEST_TOKEN);

        assertEquals(dto, result);
        verify(itemRepository).save(entity);
    }

    @Test
    void update_ShouldUpdateAndReturnDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(itemRepository.save(entity)).thenReturn(entity);
        when(itemMapper.toDto(entity)).thenReturn(dto);

        ItemDto result = itemService.update(1L, dto, TEST_TOKEN);

        assertEquals(dto, result);
        verify(itemMapper).updateEntity(entity, dto);
        verify(itemRepository).save(entity);
    }

    @Test
    void update_ShouldThrowIfNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.update(1L, dto, TEST_TOKEN));
    }

    @Test
    void delete_ShouldDeleteIfExists() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        itemService.delete(1L);
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowIfNotFound() {
        when(itemRepository.existsById(1L)).thenReturn(false);
        assertThrows(ItemNotFoundException.class, () -> itemService.delete(1L));
    }

    @Test
    void findById_ShouldReturnDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(itemMapper.toDto(entity)).thenReturn(dto);

        ItemDto result = itemService.findById(1L, TEST_TOKEN);

        assertEquals(dto, result);
        verify(itemRepository).findById(1L);
    }

    @Test
    void findById_ShouldThrowIfNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.findById(1L, TEST_TOKEN));
    }

    @Test
    void searchItems_ShouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> page = new PageImpl<>(List.of(entity));

        when(itemRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
                .thenReturn(page);
        when(itemMapper.toDto(entity)).thenReturn(dto);

        Page<ItemDto> result = itemService.searchItems("pen", "10.0", "Pencil", TEST_TOKEN, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().getFirst());
    }

    @Test
    void searchItems_ShouldWorkWithNullParameters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> page = new PageImpl<>(List.of(entity));

        when(itemRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
                .thenReturn(page);
        when(itemMapper.toDto(entity)).thenReturn(dto);

        Page<ItemDto> result = itemService.searchItems(null, null, null, TEST_TOKEN, pageable);

        assertEquals(1, result.getTotalElements());
        verify(itemRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable));
    }
}
