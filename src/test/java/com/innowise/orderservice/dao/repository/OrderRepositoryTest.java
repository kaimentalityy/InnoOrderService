package com.innowise.orderservice.dao.repository;

import com.innowise.orderservice.integration.BaseIntegrationTest;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        order = new Order();
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUserId(123L);
        order.setCreatedDate(LocalDateTime.now());

        orderRepository.save(order);
    }

    @Test
    void shouldSaveAndFindOrderById() {
        Optional<Order> found = orderRepository.findById(order.getId());

        assertTrue(found.isPresent(), "Order should be found by ID");
        assertEquals(order.getStatus(), found.get().getStatus());
        assertEquals(order.getUserId(), found.get().getUserId());

        assertEquals(order.getCreatedDate().truncatedTo(ChronoUnit.MILLIS),
                found.get().getCreatedDate().truncatedTo(ChronoUnit.MILLIS));
    }


    @Test
    void shouldFindAllOrders() {
        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size(), "There should be exactly one order in the database");
    }

    @Test
    void shouldUpdateOrderStatus() {
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        Order updated = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.CONFIRMED, updated.getStatus(), "Order status should be updated");
    }

    @Test
    void shouldDeleteOrder() {
        orderRepository.delete(order);
        List<Order> orders = orderRepository.findAll();
        assertTrue(orders.isEmpty(), "Repository should be empty after deletion");
    }
}
