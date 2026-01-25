package com.innowise.mapper;

import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "order.id", target = "orderId")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(source = "itemId", target = "item.id")
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget OrderItem entity, OrderItemDto dto);
}

