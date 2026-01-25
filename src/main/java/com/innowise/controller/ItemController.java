package com.innowise.controller;

import com.innowise.model.dto.ItemDto;
import com.innowise.service.ItemService;
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
 * REST controller for managing items.
 * Provides endpoints for creating, updating, deleting, retrieving, and
 * searching items.
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Item Management", description = "APIs for managing items including creation, updates, deletion, and search operations")
@SecurityRequirement(name = "Bearer Authentication")
public class ItemController {

        private final ItemService itemService;

        @Operation(summary = "Create a new item", description = "Creates a new item with the provided details. Requires JWT authentication.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Item created successfully", content = @Content(schema = @Schema(implementation = ItemDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
        })
        @PostMapping
        public ResponseEntity<ItemDto> create(
                        @Parameter(description = "Item details to create", required = true) @Valid @RequestBody ItemDto itemDto,
                        @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {

                String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
                ItemDto created = itemService.create(itemDto, token);
                return ResponseEntity
                                .created(URI.create("/api/items/" + created.id()))
                                .body(created);
        }

        @Operation(summary = "Update an existing item", description = "Updates an item identified by its ID with new data. Requires JWT authentication.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Item updated successfully", content = @Content(schema = @Schema(implementation = ItemDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
                        @ApiResponse(responseCode = "404", description = "Item not found")
        })
        @PutMapping("/{id}")
        public ResponseEntity<ItemDto> update(
                        @Parameter(description = "ID of the item to update", required = true) @PathVariable Long id,
                        @Parameter(description = "Updated item details", required = true) @Valid @RequestBody ItemDto itemDto,
                        @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {

                String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
                return ResponseEntity.ok(itemService.update(id, itemDto, token));
        }

        @Operation(summary = "Delete an item", description = "Deletes an item identified by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Item not found")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(
                        @Parameter(description = "ID of the item to delete", required = true) @PathVariable Long id) {
                itemService.delete(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Get item by ID", description = "Retrieves an item by its unique identifier. Requires JWT authentication.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Item found", content = @Content(schema = @Schema(implementation = ItemDto.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
                        @ApiResponse(responseCode = "404", description = "Item not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ItemDto> getById(
                        @Parameter(description = "ID of the item to retrieve", required = true) @PathVariable Long id,
                        @Parameter(description = "JWT authentication token", required = true) @RequestHeader("Authorization") String jwtToken) {

                String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
                return ResponseEntity.ok(itemService.findById(id, token));
        }

        @Operation(summary = "Search items", description = "Search and filter items based on name and price criteria with pagination support. Requires JWT authentication.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Items retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
        })
        @GetMapping
        public ResponseEntity<Page<ItemDto>> search(
                        @Parameter(description = "Filter by item name (partial match)") @RequestParam(required = false) String name,
                        @Parameter(description = "Filter by exact item name") @RequestParam(required = false) String exactName,
                        @Parameter(description = "Filter by price") @RequestParam(required = false) String price,
                        @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
                        @Parameter(description = "JWT authentication token") @RequestHeader(value = "Authorization", required = false) String jwtToken) {

                String token = (jwtToken != null && jwtToken.startsWith("Bearer ")) ? jwtToken.substring(7) : jwtToken;
                System.out.println("ItemController.search called with name=" + name + ", token="
                                + (token != null ? "present" : "null"));
                PageRequest pageRequest = PageRequest.of(page, size);
                Page<ItemDto> results = itemService.searchItems(name, price, exactName, token, pageRequest);
                return ResponseEntity.ok(results);
        }
}
