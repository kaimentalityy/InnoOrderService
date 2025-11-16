package com.innowise.orderservice.service;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.dao.repository.OrderRepository;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.dto.OrderDto;
import com.innowise.orderservice.model.dto.UserInfoDto;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.enums.OrderStatus;
import com.innowise.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order entity;
    private OrderDto dto;
    private UserInfoDto userInfo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        entity = new Order();
        entity.setId(1L);
        entity.setStatus(OrderStatus.CONFIRMED);
        entity.setUserId(123L);
        entity.setCreatedDate(LocalDateTime.now());

        userInfo = new UserInfoDto(1L, "John", "Doe", "john@example.com");

        dto = new OrderDto(
                1L,
                123L,
                OrderStatus.CONFIRMED,
                entity.getCreatedDate(),
                List.of(),
                userInfo
        );
    }

    @Test
    void create_ShouldReturnDtoWithUserInfo() {
        when(orderMapper.toEntity(dto)).thenReturn(entity);
        when(orderRepository.save(entity)).thenReturn(entity);
        when(orderMapper.toDto(entity)).thenReturn(dto);
        when(userServiceClient.getUserById(entity.getUserId())).thenReturn(userInfo);

        OrderDto result = orderService.create(dto);

        assertEquals(dto.id(), result.id());
        assertEquals(dto.userId(), result.userId());
        assertEquals(dto.status(), result.status());
        assertEquals(dto.createdDate(), result.createdDate());
        assertEquals(userInfo, result.userInfo());
        verify(orderRepository).save(entity);
    }

    @Test
    void update_ShouldReturnUpdatedDto() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderRepository.save(entity)).thenReturn(entity);
        when(orderMapper.toDto(entity)).thenReturn(dto);
        when(userServiceClient.getUserById(entity.getUserId())).thenReturn(userInfo);

        OrderDto result = orderService.update(1L, dto);

        verify(orderMapper).updateEntity(entity, dto);
        verify(orderRepository).save(entity);
        assertEquals(dto.id(), result.id());
        assertEquals(userInfo, result.userInfo());
    }

    @Test
    void update_ShouldThrowIfNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.update(1L, dto));
    }

    @Test
    void delete_ShouldCallRepository() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        orderService.delete(1L);
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowIfNotFound() {
        when(orderRepository.existsById(1L)).thenReturn(false);
        assertThrows(OrderNotFoundException.class, () -> orderService.delete(1L));
    }

    @Test
    void findById_ShouldReturnDtoWithUserInfo() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderMapper.toDto(entity)).thenReturn(dto);
        when(userServiceClient.getUserById(entity.getUserId())).thenReturn(userInfo);

        OrderDto result = orderService.findById(1L);

        assertEquals(dto.id(), result.id());
        assertEquals(userInfo, result.userInfo());
    }

    @Test
    void findById_ShouldThrowIfNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.findById(1L));
    }

    @Test
    void searchOrders_ShouldReturnDtosWithUserInfo() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(entity));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(orderMapper.toDto(entity)).thenReturn(dto);
        when(userServiceClient.getUserById(entity.getUserId())).thenReturn(userInfo);

        Page<OrderDto> result = orderService.searchOrders(123L, null, "NEW", null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(userInfo, result.getContent().get(0).userInfo());
    }

    @Test
    void searchOrders_ShouldUseEmailIfProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(entity));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(orderMapper.toDto(entity)).thenReturn(dto);
        when(userServiceClient.getUserByEmail("john@example.com")).thenReturn(userInfo);

        Page<OrderDto> result = orderService.searchOrders(null, "john@example.com", "NEW", null, null, pageable);

        assertEquals(userInfo, result.getContent().get(0).userInfo());
    }
}
