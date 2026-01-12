package com.innowise.mapper;

import com.innowise.model.dto.ItemDto;
import com.innowise.model.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toDto(Item entity);

    Item toEntity(ItemDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Item entity, ItemDto dto);
}
