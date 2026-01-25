package com.innowise.controller;

import com.innowise.model.dto.OrderDto;
import com.innowise.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
@Tag(name = "Order Management", description = "APIs for managing orders including creation, updates, deletion, and search operations")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order", description = "Creates a new order with the provided details. Requires JWT authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully", content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @PostMapping
    public ResponseEntity<OrderDto> create(
            @Parameter(description = "Order details to create", required = true) @Valid @RequestBody OrderDto orderDto,
            @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {

        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

        OrderDto created = orderService.create(orderDto, token);
        return ResponseEntity
                .created(URI.create("/api/orders/" + created.id()))
                .body(created);
    }

    @Operation(summary = "Update an existing order", description = "Updates an order identified by its ID with new data. Requires JWT authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully", content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(
            @Parameter(description = "ID of the order to update", required = true) @PathVariable Long id,
            @Parameter(description = "Updated order details", required = true) @RequestBody OrderDto orderDto,
            @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {

        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return ResponseEntity.ok(orderService.update(id, orderDto, token));
    }

    @Operation(summary = "Delete an order", description = "Deletes an order identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the order to delete", required = true) @PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get order by ID", description = "Retrieves an order by its unique identifier. Requires JWT authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found", content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(
            @Parameter(description = "ID of the order to retrieve", required = true) @PathVariable Long id,
            @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {
        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return ResponseEntity.ok(orderService.findById(id, token));
    }

    @Operation(summary = "Search orders", description = "Search and filter orders based on various criteria with pagination support. Requires JWT authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping
    public ResponseEntity<Page<OrderDto>> search(
            @Parameter(description = "Filter by user ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "Filter by user email") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by order status (e.g., PENDING, COMPLETED, CANCELLED)") @RequestParam(required = false) String status,
            @Parameter(description = "Filter orders created after this date-time") @RequestParam(required = false) LocalDateTime createdAfter,
            @Parameter(description = "Filter orders created before this date-time") @RequestParam(required = false) LocalDateTime createdBefore,
            @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken,
            @Parameter(description = "Pagination information (page, size, sort)") Pageable pageable) {

        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

        return ResponseEntity.ok(
                orderService.searchOrders(userId, email, status, createdAfter, createdBefore, token, pageable));
    }
}
