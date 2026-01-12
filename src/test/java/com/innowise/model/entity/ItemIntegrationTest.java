package com.innowise.model.entity;

import com.innowise.dao.repository.ItemRepository;
import com.innowise.integration.BaseIntegrationTest;
import jakarta.persistence.Column;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class ItemIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();

        item = new Item();
        item.setName("Laptop");
        item.setPrice(BigDecimal.valueOf(999.99));
        item = itemRepository.saveAndFlush(item);
    }

    @Test
    void testItemIsPersistedCorrectly() {
        Optional<Item> savedItemOpt = itemRepository.findById(item.getId());
        assertThat(savedItemOpt).isPresent();

        Item savedItem = savedItemOpt.get();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Laptop");
        assertThat(savedItem.getPrice()).isEqualByComparingTo("999.99");
    }

    @Test
    void testItemUpdate() {
        Item savedItem = itemRepository.findById(item.getId()).orElseThrow();
        savedItem.setName("Updated Laptop");
        savedItem.setPrice(BigDecimal.valueOf(1099.99));
        itemRepository.saveAndFlush(savedItem);

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("Updated Laptop");
        assertThat(updatedItem.getPrice()).isEqualByComparingTo("1099.99");
    }

    @Test
    void testItemDeletion() {
        itemRepository.delete(item);
        itemRepository.flush();

        assertThat(itemRepository.findById(item.getId())).isEmpty();
    }

    @Test
    void testEqualsAndHashCode() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test");
        item1.setPrice(BigDecimal.TEN);

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("Test");
        item2.setPrice(BigDecimal.TEN);

        Item item3 = new Item();
        item3.setId(2L);
        item3.setName("Other");
        item3.setPrice(BigDecimal.ONE);

        assertThat(item1).isEqualTo(item2);
        assertThat(item1).hasSameHashCodeAs(item2);
        assertThat(item1).isNotEqualTo(item3);
    }

    @Test
    void testToStringContainsAllFields() {
        String toString = item.toString();
        assertThat(toString).contains("Laptop");
        assertThat(toString).contains("999.99");
        assertThat(toString).contains("id=");
    }

    @Test
    void testGettersAndSetters() {
        Item newItem = new Item();
        newItem.setId(100L);
        newItem.setName("Phone");
        newItem.setPrice(BigDecimal.valueOf(499.50));

        assertThat(newItem.getId()).isEqualTo(100L);
        assertThat(newItem.getName()).isEqualTo("Phone");
        assertThat(newItem.getPrice()).isEqualByComparingTo("499.50");
    }

    @Test
    void testNameColumnLengthAndNotNull() throws NoSuchFieldException {
        Field nameField = Item.class.getDeclaredField("name");
        Column column = nameField.getAnnotation(Column.class);
        assertThat(column).isNotNull();
        assertThat(column.length()).isEqualTo(50);
        assertThat(column.nullable()).isFalse();
    }

    @Test
    void testPriceColumnNotNull() throws NoSuchFieldException {
        Field priceField = Item.class.getDeclaredField("price");
        Column column = priceField.getAnnotation(Column.class);
        assertThat(column).isNotNull();
        assertThat(column.nullable()).isFalse();
    }

    @Test
    void testCannotSaveItemWithNullFields() {
        Item invalidItem = new Item();
        invalidItem.setName(null);
        invalidItem.setPrice(null);

        assertThrows(Exception.class, () -> {
            itemRepository.saveAndFlush(invalidItem);
        });
    }
}
