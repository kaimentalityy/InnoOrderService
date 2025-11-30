package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.OrderDto;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.time.LocalDateTime;

/**
 * REST controller for managing orders.
 * Provides endpoints for creating, updating, deleting, retrieving, and
 * searching orders.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order.
     *
     * @param orderDto the order data transfer object containing order details
     * @return the created order DTO wrapped in a ResponseEntity with HTTP 201
     *         Created status
     */
    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto orderDto) {
        OrderDto created = orderService.create(orderDto);
        return ResponseEntity
                .created(URI.create("/api/orders/" + created.id()))
                .body(created);
    }

    /**
     * Updates an existing order.
     *
     * @param id       the ID of the order to update
     * @param orderDto the updated order data
     * @return the updated order DTO wrapped in a ResponseEntity with HTTP 200 OK
     *         status
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.update(id, orderDto));
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the ID of the order to delete
     * @return a ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return the order DTO wrapped in a ResponseEntity with HTTP 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    /**
     * Searches for orders based on various criteria.
     *
     * @param userId        optional user ID to filter by
     * @param email         optional email to filter by
     * @param status        optional order status to filter by
     * @param createdAfter  optional start date for creation timestamp filtering
     * @param createdBefore optional end date for creation timestamp filtering
     * @param pageable      pagination information
     * @return a page of order DTOs matching the criteria
     */
    @GetMapping
    public ResponseEntity<Page<OrderDto>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime createdAfter,
            @RequestParam(required = false) LocalDateTime createdBefore,
            Pageable pageable) {

        return ResponseEntity.ok(
                orderService.searchOrders(userId, email, status, createdAfter, createdBefore, pageable));
    }
}
