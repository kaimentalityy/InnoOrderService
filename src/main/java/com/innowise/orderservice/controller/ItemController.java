package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.ItemDto;
import com.innowise.orderservice.service.ItemService;
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
 * REST controller for managing items.
 * Provides endpoints for creating, updating, deleting, retrieving, and
 * searching items.
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * Creates a new item.
     *
     * @param itemDto the item data transfer object containing item details
     * @return the created item DTO wrapped in a ResponseEntity with HTTP 201
     *         Created status
     */
    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto itemDto) {
        ItemDto created = itemService.create(itemDto);
        return ResponseEntity
                .created(URI.create("/api/items/" + created.id()))
                .body(created);
    }

    /**
     * Updates an existing item.
     *
     * @param id      the ID of the item to update
     * @param itemDto the updated item data
     * @return the updated item DTO wrapped in a ResponseEntity with HTTP 200 OK
     *         status
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> update(@PathVariable Long id, @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.update(id, itemDto));
    }

    /**
     * Deletes an item by its ID.
     *
     * @param id the ID of the item to delete
     * @return a ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves an item by its ID.
     *
     * @param id the ID of the item to retrieve
     * @return the item DTO wrapped in a ResponseEntity with HTTP 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    /**
     * Searches for items based on various criteria.
     *
     * @param name      optional name to filter by
     * @param exactName optional exact name to filter by
     * @param price     optional price to filter by
     * @param page      page number (default 0)
     * @param size      page size (default 10)
     * @return a page of item DTOs matching the criteria
     */
    @GetMapping
    public ResponseEntity<Page<ItemDto>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String exactName,
            @RequestParam(required = false) String price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ItemDto> results = itemService.searchItems(name, price, exactName, pageRequest);
        return ResponseEntity.ok(results);
    }
}
