package com.innowise.controller;

import com.innowise.model.dto.OrderItemDto;
import com.innowise.service.OrderItemService;
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
import org.springframework.data.domain.PageRequest;
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

/**
 * REST controller for managing order items.
 * Provides endpoints for creating, updating, deleting, and retrieving order
 * items.
 */
@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@Tag(name = "Order Item Management", description = "APIs for managing order items including creation, updates, deletion, and search operations")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Operation(summary = "Create a new order item", description = "Creates a new order item with the provided details. Requires JWT authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order item created successfully", content = @Content(schema = @Schema(implementation = OrderItemDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @PostMapping
    public ResponseEntity<OrderItemDto> create(
            @Parameter(description = "Order item details to create", required = true) @Valid @RequestBody OrderItemDto orderItemDto,
            @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {

        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        OrderItemDto created = orderItemService.create(orderItemDto, token);
        return ResponseEntity
                .created(URI.create("/api/order-items/" + created.id()))
                .body(created);
    }

    @Operation(summary = "Update an existing order item", description = "Updates an order item identified by its ID with new data. Requires JWT authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order item updated successfully", content = @Content(schema = @Schema(implementation = OrderItemDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Order item not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> update(
            @Parameter(description = "ID of the order item to update", required = true) @PathVariable Long id,
            @Parameter(description = "Updated order item details", required = true) @Valid @RequestBody OrderItemDto orderItemDto,
            @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {

        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return ResponseEntity.ok(orderItemService.update(id, orderItemDto, token));
    }

    @Operation(summary = "Delete an order item", description = "Deletes an order item identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order item not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the order item to delete", required = true) @PathVariable Long id) {
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get order item by ID", description = "Retrieves an order item by its unique identifier. Requires JWT authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order item found", content = @Content(schema = @Schema(implementation = OrderItemDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Order item not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getById(
            @Parameter(description = "ID of the order item to retrieve", required = true) @PathVariable Long id,
            @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {

        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return ResponseEntity.ok(orderItemService.findById(id, token));
    }

    @Operation(summary = "Search order items", description = "Search and filter order items based on various criteria with pagination support. Requires JWT authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order items retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping
    public ResponseEntity<Page<OrderItemDto>> search(
            @Parameter(description = "Filter by order ID") @RequestParam(required = false) Long orderId,
            @Parameter(description = "Filter by item ID") @RequestParam(required = false) Long itemId,
            @Parameter(description = "Filter by exact quantity") @RequestParam(required = false) Integer quantity,
            @Parameter(description = "Filter by minimum quantity") @RequestParam(required = false) Integer minQuantity,
            @Parameter(description = "Filter by maximum quantity") @RequestParam(required = false) Integer maxQuantity,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {

        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderItemDto> results = orderItemService.searchOrderItems(
                orderId, itemId, quantity, minQuantity, maxQuantity, token, pageRequest);
        return ResponseEntity.ok(results);
    }
}
