package com.innowise.orderservice.dao.repository;

import com.innowise.orderservice.integration.BaseIntegrationTest;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderItemRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Order order;
    private Item item;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        itemRepository.deleteAll();
        orderRepository.deleteAll();

        order = new Order();
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUserId(123L);
        order.setCreatedDate(LocalDateTime.now());
        orderRepository.save(order);

        item = new Item();
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(12.5));
        itemRepository.save(item);

        orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(3);
        orderItemRepository.save(orderItem);
    }

    @Test
    void shouldSaveAndFindOrderItemById() {
        Optional<OrderItem> found = orderItemRepository.findById(orderItem.getId());

        assertTrue(found.isPresent(), "Order item should be found by ID");
        assertEquals(orderItem.getQuantity(), found.get().getQuantity());
        assertEquals(order.getId(), found.get().getOrder().getId());
        assertEquals(item.getId(), found.get().getItem().getId());
    }

    @Test
    void shouldFindAllOrderItems() {
        List<OrderItem> items = orderItemRepository.findAll();
        assertEquals(1, items.size(), "There should be exactly one order item in the database");
    }

    @Test
    void shouldUpdateOrderItemQuantity() {
        orderItem.setQuantity(5);
        orderItemRepository.save(orderItem);

        OrderItem updated = orderItemRepository.findById(orderItem.getId()).orElseThrow();
        assertEquals(5, updated.getQuantity(), "Order item quantity should be updated");
    }

    @Test
    void shouldDeleteOrderItem() {
        orderItemRepository.delete(orderItem);
        List<OrderItem> items = orderItemRepository.findAll();
        assertTrue(items.isEmpty(), "Repository should be empty after deletion");
    }
}
