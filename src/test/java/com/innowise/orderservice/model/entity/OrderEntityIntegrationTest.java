package com.innowise.orderservice.model.entity;

import com.innowise.orderservice.dao.repository.ItemRepository;
import com.innowise.orderservice.dao.repository.OrderItemRepository;
import com.innowise.orderservice.dao.repository.OrderRepository;
import com.innowise.orderservice.integration.BaseIntegrationTest;
import com.innowise.orderservice.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Transactional
class OrderEntityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Order order;
    private Item item;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        item = new Item();
        item.setName("Laptop");
        item.setPrice(BigDecimal.valueOf(999.99));
        item = itemRepository.saveAndFlush(item);

        order = new Order();
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUserId(123L);
        order.setCreatedDate(LocalDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(2);

        order.getItems().add(orderItem);

        order = orderRepository.saveAndFlush(order);
    }

    @Test
    void testOrderIsPersistedCorrectly() {
        Order savedOrder = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(savedOrder.getUserId()).isEqualTo(123L);
        assertThat(savedOrder.getCreatedDate()).isNotNull();
    }

    @Test
    void testOneToManyItemsRelationship() {
        Order managedOrder = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(managedOrder.getItems())
                .hasSize(1)
                .allSatisfy(orderItem -> {
                    assertThat(orderItem.getItem().getName()).isEqualTo("Laptop");
                    assertThat(orderItem.getQuantity()).isEqualTo(2);
                    assertThat(orderItem.getItem().getPrice()).isEqualByComparingTo("999.99");
                });

        managedOrder.getItems().forEach(orderItem -> {
            assertThat(orderItem.getOrder()).isNotNull();
            assertThat(orderItem.getItem()).isNotNull();
            assertThat(orderItem.getQuantity()).isEqualTo(2);
        });
    }

    @Test
    void testCascadeAndOrphanRemoval() {
        Order managedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(orderItemRepository.count()).isEqualTo(1);

        managedOrder.getItems().clear();
        orderRepository.saveAndFlush(managedOrder);

        assertThat(orderItemRepository.count()).isZero();
    }

    @Test
    void testEqualsHashCodeAndToString() {
        Order order1 = new Order();
        order1.setId(order.getId());
        order1.setStatus(order.getStatus());
        order1.setUserId(order.getUserId());
        order1.setCreatedDate(order.getCreatedDate());
        order1.setItems(order.getItems());

        Order order2 = new Order();
        order2.setId(order.getId());
        order2.setStatus(order.getStatus());
        order2.setUserId(order.getUserId());
        order2.setCreatedDate(order.getCreatedDate());
        order2.setItems(order.getItems());

        Order order3 = new Order();
        order3.setId(999L);
        order3.setStatus(OrderStatus.CONFIRMED);
        order3.setUserId(999L);
        order3.setCreatedDate(LocalDateTime.now());

        assertThat(order1).isEqualTo(order2);
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
        assertNotEquals(order1, order3);

        String toString = order1.toString();
        assertThat(toString).contains("CONFIRMED");
        assertThat(toString).contains("123");
        assertThat(toString).doesNotContain("items=");
    }

    @Test
    void testSettersAndGettersIndividually() {
        Order o = new Order();
        o.setId(555L);
        o.setStatus(OrderStatus.PAYMENT_PENDING);
        o.setUserId(987L);
        LocalDateTime now = LocalDateTime.now();
        o.setCreatedDate(now);

        assertThat(o.getId()).isEqualTo(555L);
        assertThat(o.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
        assertThat(o.getUserId()).isEqualTo(987L);
        assertThat(o.getCreatedDate()).isEqualTo(now);

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrder(o);
        orderItem.setQuantity(1);
        o.getItems().add(orderItem);
        assertThat(o.getItems()).hasSize(1);
    }
}
