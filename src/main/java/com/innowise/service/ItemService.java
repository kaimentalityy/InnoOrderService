package com.innowise.service;

import com.innowise.model.dto.ItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service for managing {@link ItemDto} entities.
 */
public interface ItemService extends CrudService<ItemDto, Long> {

    Page<ItemDto> searchItems(String name,
                              String price,
                              String exactName,
                              String jwtToken,
                              Pageable pageable);
}
