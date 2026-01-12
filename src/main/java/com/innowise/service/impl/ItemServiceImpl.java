package com.innowise.service.impl;

import com.innowise.dao.repository.ItemRepository;
import com.innowise.dao.specification.ItemsSpecifications;
import com.innowise.exception.ItemNotFoundException;
import com.innowise.mapper.ItemMapper;
import com.innowise.model.dto.ItemDto;
import com.innowise.model.entity.Item;
import com.innowise.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto createDto, String jwtToken) {
        Item saved = itemRepository.save(itemMapper.toEntity(createDto));
        return itemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long id, ItemDto updateDto, String jwtToken) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException());
        itemMapper.updateEntity(existing, updateDto);
        return itemMapper.toDto(itemRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException();
        }
        itemRepository.deleteById(id);
    }

    @Override
    public ItemDto findById(Long id, String jwtToken) {
        return itemRepository.findById(id)
                .map(itemMapper::toDto)
                .orElseThrow(() -> new ItemNotFoundException());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDto> searchItems(String name, String price, String exactName, String jwtToken, Pageable pageable) {
        Specification<Item> spec = Specification.where(null);
        if (name != null) spec = spec.and(ItemsSpecifications.hasName(name));
        if (exactName != null) spec = spec.and(ItemsSpecifications.hasExactName(exactName));
        if (price != null) spec = spec.and(ItemsSpecifications.hasPrice(price));
        return itemRepository.findAll(spec, pageable)
                .map(itemMapper::toDto);
    }
}