package com.innowise.dao.specification;

import com.innowise.integration.BaseIntegrationTest;
import com.innowise.model.entity.Item;
import com.innowise.dao.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemsSpecificationsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();

        item1 = new Item();
        item1.setName("Laptop");
        item1.setPrice(BigDecimal.valueOf(999.99));
        itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Mouse");
        item2.setPrice(BigDecimal.valueOf(15.99));
        itemRepository.save(item2);
    }

    @Test
    void testHasName() {
        Specification<Item> spec = ItemsSpecifications.hasName("lap");
        List<Item> results = itemRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void testHasExactName() {
        Specification<Item> spec = ItemsSpecifications.hasExactName("mouse");
        List<Item> results = itemRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Mouse");
    }

    @Test
    void testHasPrice() {
        Specification<Item> spec = ItemsSpecifications.hasPrice("15.99");
        List<Item> results = itemRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Mouse");
        assertThat(results.get(0).getPrice()).isEqualByComparingTo("15.99");
    }
}
