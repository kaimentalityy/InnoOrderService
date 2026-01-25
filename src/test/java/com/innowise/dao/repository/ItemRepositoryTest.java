package com.innowise.dao.repository;

import com.innowise.integration.BaseIntegrationTest;
import com.innowise.model.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ItemRepository} using PostgreSQL Testcontainers and WireMock setup.
 */
@ActiveProfiles("test")
@Transactional
class ItemRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();

        item1 = new Item();
        item1.setName("Item One");
        item1.setPrice(BigDecimal.valueOf(10.5));

        item2 = new Item();
        item2.setName("Item Two");
        item2.setPrice(BigDecimal.valueOf(20.0));

        itemRepository.saveAll(List.of(item1, item2));
    }

    @Test
    void testSaveAndFindById() {
        Optional<Item> found = itemRepository.findById(item1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Item One");
        assertThat(found.get().getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10.5));
    }

    @Test
    void testFindAll() {
        List<Item> items = itemRepository.findAll();

        assertThat(items)
                .hasSize(2)
                .extracting(Item::getName)
                .containsExactlyInAnyOrder("Item One", "Item Two");
    }

    @Test
    void testUpdate() {
        item1.setName("Updated Item");
        item1.setPrice(BigDecimal.valueOf(15.0));
        itemRepository.save(item1);

        Optional<Item> updated = itemRepository.findById(item1.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Item");
        assertThat(updated.get().getPrice()).isEqualByComparingTo(BigDecimal.valueOf(15.0));
    }

    @Test
    void testDelete() {
        itemRepository.delete(item2);
        List<Item> items = itemRepository.findAll();

        assertThat(items)
                .hasSize(1)
                .extracting(Item::getId)
                .containsExactly(item1.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Item> notFound = itemRepository.findById(9999L);
        assertThat(notFound).isEmpty();
    }
}
