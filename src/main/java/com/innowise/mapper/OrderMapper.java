package com.innowise.mapper;

import com.innowise.model.dto.OrderDto;
import com.innowise.model.dto.OrderItemDto;
import com.innowise.model.entity.Order;
import com.innowise.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "items", source = "items", qualifiedByName = "orderItemsToDtos")
    OrderDto toDto(Order order);

    @Mapping(target = "items", ignore = true)
    Order toEntity(OrderDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Order entity, OrderDto dto);

    @Named("orderItemsToDtos")
    default List<OrderItemDto> orderItemsToDtos(List<OrderItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .filter(item -> item != null && item.getItem() != null && item.getOrder() != null)
                .map(item -> new OrderItemDto(
                        item.getId(),
                        item.getOrder().getId(),
                        item.getItem().getId(),
                        item.getQuantity()
                ))
                .toList();
    }
}
