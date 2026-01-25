package com.innowise.dao.specification;

import com.innowise.model.entity.OrderItem;
import org.springframework.data.jpa.domain.Specification;

public class OrderItemSpecifications {

    public static Specification<OrderItem> hasOrderId(Long orderId) {
        return (root, query, cb) ->
                cb.equal(root.get("order").get("id"), orderId);
    }

    public static Specification<OrderItem> hasItemId(Long itemId) {
        return (root, query, cb) ->
                cb.equal(root.get("item").get("id"), itemId);
    }

    public static Specification<OrderItem> hasQuantity(Integer quantity) {
        return (root, query, cb) ->
                cb.equal(root.get("quantity"), quantity);
    }

    public static Specification<OrderItem> quantityGreaterThan(Integer minQuantity) {
        return (root, query, cb) ->
                cb.greaterThan(root.get("quantity"), minQuantity);
    }

    public static Specification<OrderItem> quantityLessThan(Integer maxQuantity) {
        return (root, query, cb) ->
                cb.lessThan(root.get("quantity"), maxQuantity);
    }
}
