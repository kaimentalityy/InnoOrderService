package com.innowise.service;

import com.innowise.dao.repository.OrderItemRepository;
import com.innowise.exception.OrderItemNotFoundException;
import com.innowise.mapper.OrderItemMapper;
import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.entity.OrderItem;
import com.innowise.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem entity;
    private OrderItemDto dto;
    private static final String TEST_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entity = new OrderItem();
        entity.setId(1L);

        dto = new OrderItemDto(
                1L,
                1L,
                1L,
                5);
    }

    @Test
    void create_ShouldReturnCreatedOrderItemDto() {
        when(orderItemMapper.toEntity(dto)).thenReturn(entity);
        when(orderItemRepository.save(entity)).thenReturn(entity);
        when(orderItemMapper.toDto(entity)).thenReturn(dto);

        OrderItemDto result = orderItemService.create(dto, TEST_TOKEN);

        assertNotNull(result);
        assertEquals(dto.id(), result.id());
        verify(orderItemMapper).toEntity(dto);
        verify(orderItemRepository).save(entity);
        verify(orderItemMapper).toDto(entity);
    }

    @Test
    void update_ShouldUpdateExistingOrderItem() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderItemRepository.save(entity)).thenReturn(entity);
        when(orderItemMapper.toDto(entity)).thenReturn(dto);

        OrderItemDto result = orderItemService.update(1L, dto, TEST_TOKEN);

        assertEquals(dto, result);
        verify(orderItemMapper).updateEntity(entity, dto);
        verify(orderItemRepository).save(entity);
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderItemNotFoundException.class,
                () -> orderItemService.update(1L, dto, TEST_TOKEN));

        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDelete_WhenExists() {
        when(orderItemRepository.existsById(1L)).thenReturn(true);

        orderItemService.delete(1L);

        verify(orderItemRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrow_WhenNotExists() {
        when(orderItemRepository.existsById(1L)).thenReturn(false);

        assertThrows(OrderItemNotFoundException.class,
                () -> orderItemService.delete(1L));

        verify(orderItemRepository, never()).deleteById(any());
    }

    @Test
    void findById_ShouldReturnOrderItemDto_WhenExists() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderItemMapper.toDto(entity)).thenReturn(dto);

        OrderItemDto result = orderItemService.findById(1L, TEST_TOKEN);

        assertNotNull(result);
        assertEquals(dto.id(), result.id());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderItemNotFoundException.class,
                () -> orderItemService.findById(1L, TEST_TOKEN));
    }

    @Test
    void searchOrderItems_ShouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderItem> page = new PageImpl<>(List.of(entity));

        when(orderItemRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(orderItemMapper.toDto(entity)).thenReturn(dto);

        Page<OrderItemDto> result = orderItemService.searchOrderItems(
                1L, 2L, 3, 1, 10, TEST_TOKEN, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchOrderItems_ShouldWork_WithAllNullFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderItem> page = new PageImpl<>(List.of(entity));

        when(orderItemRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(orderItemMapper.toDto(entity)).thenReturn(dto);

        Page<OrderItemDto> result = orderItemService.searchOrderItems(
                null, null, null, null, null, TEST_TOKEN, pageable);

        assertEquals(1, result.getTotalElements());
    }
}
