package com.innowise.dao.specification;

import com.innowise.integration.BaseIntegrationTest;
import com.innowise.model.entity.OrderItem;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemSpecificationsTest extends BaseIntegrationTest {

    private Root<OrderItem> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder cb;
    private Predicate predicate;

    @BeforeEach
    void setUp() {
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);
        cb = mock(CriteriaBuilder.class);
        predicate = mock(Predicate.class);
    }

    @Test
    void hasOrderId_ShouldReturnPredicate() {
        @SuppressWarnings("unchecked")
        Path<Object> orderPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> orderIdPath = mock(Path.class);

        when(root.get("order")).thenReturn(orderPath);
        when(orderPath.get("id")).thenReturn(orderIdPath);
        when(cb.equal(orderIdPath, 1L)).thenReturn(predicate);

        Predicate result = OrderItemSpecifications.hasOrderId(1L)
                .toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(predicate, result);
        verify(cb).equal(orderIdPath, 1L);
    }

    @Test
    void hasItemId_ShouldReturnPredicate() {
        @SuppressWarnings("unchecked")
        Path<Object> itemPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> itemIdPath = mock(Path.class);

        when(root.get("item")).thenReturn(itemPath);
        when(itemPath.get("id")).thenReturn(itemIdPath);
        when(cb.equal(itemIdPath, 2L)).thenReturn(predicate);

        Predicate result = OrderItemSpecifications.hasItemId(2L)
                .toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(predicate, result);
        verify(cb).equal(itemIdPath, 2L);
    }

    @Test
    void hasQuantity_ShouldReturnPredicate() {
        @SuppressWarnings("unchecked")
        Path<Integer> quantityPath = (Path<Integer>) mock(Path.class);

        when(root.get("quantity")).thenReturn((Path) quantityPath);
        when(cb.equal(quantityPath, 10)).thenReturn(predicate);

        Predicate result = OrderItemSpecifications.hasQuantity(10)
                .toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(predicate, result);
        verify(cb).equal(quantityPath, 10);
    }

    @Test
    void quantityGreaterThan_ShouldReturnPredicate() {
        @SuppressWarnings("unchecked")
        Path<Integer> quantityPath = (Path<Integer>) mock(Path.class);

        when(root.get("quantity")).thenReturn((Path) quantityPath);
        when(cb.greaterThan(quantityPath, 5)).thenReturn(predicate);

        Predicate result = OrderItemSpecifications.quantityGreaterThan(5)
                .toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(predicate, result);
        verify(cb).greaterThan(quantityPath, 5);
    }

    @Test
    void quantityLessThan_ShouldReturnPredicate() {
        @SuppressWarnings("unchecked")
        Path<Integer> quantityPath = (Path<Integer>) mock(Path.class);

        when(root.get("quantity")).thenReturn((Path) quantityPath);
        when(cb.lessThan(quantityPath, 20)).thenReturn(predicate);

        Predicate result = OrderItemSpecifications.quantityLessThan(20)
                .toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(predicate, result);
        verify(cb).lessThan(quantityPath, 20);
    }
}
