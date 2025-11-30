package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.OrderItemDto;
import com.innowise.orderservice.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * REST controller for managing order items.
 * Provides endpoints for creating, updating, deleting, and retrieving order
 * items.
 */
@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * Creates a new order item.
     *
     * @param orderItemDto the order item data transfer object containing details
     * @return the created order item DTO wrapped in a ResponseEntity with HTTP 201
     *         Created status
     */
    @PostMapping
    public ResponseEntity<OrderItemDto> create(@Valid @RequestBody OrderItemDto orderItemDto) {
        OrderItemDto created = orderItemService.create(orderItemDto);
        return ResponseEntity
                .created(URI.create("/api/order-items/" + created.id()))
                .body(created);
    }

    /**
     * Updates an existing order item.
     *
     * @param id           the ID of the order item to update
     * @param orderItemDto the updated order item data
     * @return the updated order item DTO wrapped in a ResponseEntity with HTTP 200
     *         OK status
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> update(@PathVariable Long id, @Valid @RequestBody OrderItemDto orderItemDto) {
        return ResponseEntity.ok(orderItemService.update(id, orderItemDto));
    }

    /**
     * Deletes an order item by its ID.
     *
     * @param id the ID of the order item to delete
     * @return a ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves an order item by its ID.
     *
     * @param id the ID of the order item to retrieve
     * @return the order item DTO wrapped in a ResponseEntity with HTTP 200 OK
     *         status
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.findById(id));
    }

    /**
     * Searches for order items based on various criteria with pagination.
     *
     * @param orderId     (Optional) The ID of the order to filter by.
     * @param itemId      (Optional) The ID of the item to filter by.
     * @param quantity    (Optional) The exact quantity to filter by.
     * @param minQuantity (Optional) The minimum quantity to filter by.
     * @param maxQuantity (Optional) The maximum quantity to filter by.
     * @param page        The page number for pagination (default is 0).
     * @param size        The number of items per page (default is 10).
     * @return A page of order item DTOs matching the criteria, wrapped in a
     *         ResponseEntity with HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<Page<OrderItemDto>> search(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderItemDto> results = orderItemService.searchOrderItems(orderId, itemId, quantity, minQuantity,
                maxQuantity, pageRequest);
        return ResponseEntity.ok(results);
    }
}
