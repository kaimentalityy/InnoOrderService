package com.innowise.dao.specification;

import com.innowise.integration.BaseIntegrationTest;
import com.innowise.model.entity.Order;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderSpecificationsTest extends BaseIntegrationTest {

    private Root<Order> root;
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
    void hasUserId_ShouldReturnPredicate() {
        @SuppressWarnings("unchecked")
        Path<Long> userIdPath = (Path<Long>) mock(Path.class);

        when(root.get("userId")).thenReturn((Path) userIdPath);
        when(cb.equal(userIdPath, 123L)).thenReturn(predicate);

        Predicate result = OrderSpecifications.hasUserId(123L)
                .toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(predicate, result);
        verify(cb).equal(userIdPath, 123L);
    }

    @Test
    void hasStatus_ShouldReturnPredicate() {
        @SuppressWarnings("unchecked")
        Path<String> statusPath = (Path<String>) mock(Path.class);

        when(root.get("status")).thenReturn((Path) statusPath);
        when(cb.equal(statusPath, "PENDING")).thenReturn(predicate);

        Predicate result = OrderSpecifications.hasStatus("PENDING")
                .toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(predicate, result);
        verify(cb).equal(statusPath, "PENDING");
    }

    @Test
    void createdAfter_ShouldReturnPredicate() {
        @SuppressWarnings("unchecked")
        Path<LocalDateTime> createdDatePath = (Path<LocalDateTime>) mock(Path.class);

        LocalDateTime date = LocalDateTime.now().minusDays(1);

        when(root.get("createdDate")).thenReturn((Path) createdDatePath);
        when(cb.greaterThan(createdDatePath, date)).thenReturn(predicate);

        Predicate result = OrderSpecifications.createdAfter(date)
                .toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(predicate, result);
        verify(cb).greaterThan(createdDatePath, date);
    }

    @Test
    void createdBefore_ShouldReturnPredicate() {
        @SuppressWarnings("unchecked")
        Path<LocalDateTime> createdDatePath = (Path<LocalDateTime>) mock(Path.class);

        LocalDateTime date = LocalDateTime.now();

        when(root.get("createdDate")).thenReturn((Path) createdDatePath);
        when(cb.lessThan(createdDatePath, date)).thenReturn(predicate);

        Predicate result = OrderSpecifications.createdBefore(date)
                .toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(predicate, result);
        verify(cb).lessThan(createdDatePath, date);
    }
}
