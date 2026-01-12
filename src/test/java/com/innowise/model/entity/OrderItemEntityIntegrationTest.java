package com.innowise.model.entity;

import com.innowise.dao.repository.ItemRepository;
import com.innowise.dao.repository.OrderItemRepository;
import com.innowise.dao.repository.OrderRepository;
import com.innowise.integration.BaseIntegrationTest;
import com.innowise.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Transactional
class OrderItemEntityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Order order;
    private Item item;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        item = new Item();
        item.setName("Phone");
        item.setPrice(BigDecimal.valueOf(499.99));
        item = itemRepository.saveAndFlush(item);

        order = new Order();
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setUserId(321L);
        order.setCreatedDate(LocalDateTime.now());
        order = orderRepository.saveAndFlush(order);

        orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(3);

        order.getItems().add(orderItem);

        order = orderRepository.saveAndFlush(order);
    }

    @Test
    void testOrderItemIsPersistedCorrectly() {
        OrderItem savedOrderItem = orderItemRepository.findAll().stream()
                .findFirst()
                .orElseThrow();

        assertThat(savedOrderItem.getId()).isNotNull();
        assertThat(savedOrderItem.getQuantity()).isEqualTo(3);
        assertThat(savedOrderItem.getOrder().getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
        assertThat(savedOrderItem.getItem().getName()).isEqualTo("Phone");
        assertThat(savedOrderItem.getItem().getPrice()).isEqualByComparingTo("499.99");
    }

    @Test
    void testOrderItemUpdate() {
        OrderItem managedOrderItem = orderItemRepository.findAll().get(0);
        managedOrderItem.setQuantity(5);
        orderItemRepository.saveAndFlush(managedOrderItem);

        OrderItem updated = orderItemRepository.findById(managedOrderItem.getId()).orElseThrow();
        assertThat(updated.getQuantity()).isEqualTo(5);
    }

    @Test
    void testCascadeDeleteFromOrder() {
        assertThat(orderItemRepository.count()).isEqualTo(1);
        assertThat(orderRepository.count()).isEqualTo(1);

        orderRepository.delete(order);
        orderRepository.flush();

        assertThat(orderRepository.findById(order.getId())).isEmpty();
        assertThat(orderItemRepository.findAll()).isEmpty();

        assertThat(itemRepository.count()).isEqualTo(1);
        assertThat(itemRepository.findAll().get(0).getName()).isEqualTo("Phone");
    }

    @Test
    void testRemoveItemFromOrderOrphanRemoval() {
        order.getItems().clear();
        orderRepository.saveAndFlush(order);

        assertThat(orderItemRepository.count()).isZero();
        assertThat(orderRepository.findById(order.getId())).isPresent();
    }

    @Test
    void testEqualsHashCodeAndToString() {
        orderItem = orderItemRepository.saveAndFlush(orderItem);

        OrderItem same = new OrderItem();
        same.setId(orderItem.getId());
        same.setOrder(order);
        same.setItem(item);
        same.setQuantity(3);

        OrderItem different = new OrderItem();
        different.setId(999L);
        different.setOrder(order);
        different.setItem(item);
        different.setQuantity(10);

        assertThat(orderItem).isEqualTo(same);
        assertThat(orderItem.hashCode()).isEqualTo(same.hashCode());
        assertNotEquals(orderItem, different);

        String toString = orderItem.toString();
        assertThat(toString).contains("quantity=3");
        assertThat(toString).contains("Phone");
        assertThat(toString).doesNotContain("order=");
    }

    @Test
    void testSettersAndGetters() {
        OrderItem oi = new OrderItem();
        oi.setId(111L);
        oi.setOrder(order);
        oi.setItem(item);
        oi.setQuantity(42);

        assertThat(oi.getId()).isEqualTo(111L);
        assertThat(oi.getOrder()).isEqualTo(order);
        assertThat(oi.getItem()).isEqualTo(item);
        assertThat(oi.getQuantity()).isEqualTo(42);

        OrderItem oi2 = new OrderItem();
        oi2.setId(111L);
        assertThat(oi).isEqualTo(oi2);
        assertThat(oi.hashCode()).isEqualTo(oi2.hashCode());
    }

    @Test
    void testDifferentObjectsNotEqual() {
        OrderItem oi1 = new OrderItem();
        oi1.setId(1L);
        OrderItem oi2 = new OrderItem();
        oi2.setId(2L);

        assertNotEquals(oi1, oi2);
        assertNotEquals(oi1.hashCode(), oi2.hashCode());
        assertThat(oi1.equals(null)).isFalse();
        assertThat(oi1.equals("some string")).isFalse();
    }
}
