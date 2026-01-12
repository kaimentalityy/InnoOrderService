package com.innowise.mapper;

import com.innowise.model.dto.ItemDto;
import com.innowise.model.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = Mappers.getMapper(ItemMapper.class);
    }

    @Test
    void toDto_ShouldMapEntityToDto() {
        Item entity = new Item();
        entity.setId(1L);
        entity.setName("Pen");
        entity.setPrice(new BigDecimal("1.50"));

        ItemDto dto = itemMapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Pen");
        assertThat(dto.price()).isEqualByComparingTo("1.50");
    }

    @Test
    void toDto_ShouldReturnNull_WhenEntityIsNull() {
        assertThat(itemMapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_ShouldMapDtoToEntity() {
        ItemDto dto = new ItemDto(2L, "Notebook", new BigDecimal("5.00"));

        Item entity = itemMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getName()).isEqualTo("Notebook");
        assertThat(entity.getPrice()).isEqualByComparingTo("5.00");
    }

    @Test
    void toEntity_ShouldReturnNull_WhenDtoIsNull() {
        assertThat(itemMapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_ShouldUpdateEntityFields_WithoutChangingId() {
        Item entity = new Item();
        entity.setId(10L);
        entity.setName("Old Name");
        entity.setPrice(new BigDecimal("2.00"));

        ItemDto dto = new ItemDto(999L, "New Name", new BigDecimal("3.50"));

        itemMapper.updateEntity(entity, dto);

        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getName()).isEqualTo("New Name");
        assertThat(entity.getPrice()).isEqualByComparingTo("3.50");
    }
}
