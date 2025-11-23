package com.innowise.orderservice.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItemEvent Tests")
class OrderItemEventTest {

    @Test
    @DisplayName("Should create event with no-args constructor")
    void testNoArgsConstructor() {
        // When
        OrderItemEvent event = new OrderItemEvent();

        // Then
        assertNotNull(event, "Event should be created");
        assertNull(event.getItemId(), "Item ID should be null");
        assertNull(event.getItemName(), "Item name should be null");
        assertNull(event.getPrice(), "Price should be null");
        assertNull(event.getQuantity(), "Quantity should be null");
    }

    @Test
    @DisplayName("Should create event with all-args constructor")
    void testAllArgsConstructor() {
        // Given
        Long itemId = 100L;
        String itemName = "Test Product";
        BigDecimal price = new BigDecimal("49.99");
        Integer quantity = 5;

        // When
        OrderItemEvent event = new OrderItemEvent(itemId, itemName, price, quantity);

        // Then
        assertEquals(itemId, event.getItemId(), "Item ID should match");
        assertEquals(itemName, event.getItemName(), "Item name should match");
        assertEquals(price, event.getPrice(), "Price should match");
        assertEquals(quantity, event.getQuantity(), "Quantity should match");
    }

    @Test
    @DisplayName("Should set and get itemId correctly")
    void testSetAndGetItemId() {
        // Given
        OrderItemEvent event = new OrderItemEvent();
        Long itemId = 200L;

        // When
        event.setItemId(itemId);

        // Then
        assertEquals(itemId, event.getItemId(), "Item ID should match the set value");
    }

    @Test
    @DisplayName("Should set and get itemName correctly")
    void testSetAndGetItemName() {
        // Given
        OrderItemEvent event = new OrderItemEvent();
        String itemName = "Laptop Computer";

        // When
        event.setItemName(itemName);

        // Then
        assertEquals(itemName, event.getItemName(), "Item name should match the set value");
    }

    @Test
    @DisplayName("Should set and get price correctly")
    void testSetAndGetPrice() {
        // Given
        OrderItemEvent event = new OrderItemEvent();
        BigDecimal price = new BigDecimal("999.99");

        // When
        event.setPrice(price);

        // Then
        assertEquals(price, event.getPrice(), "Price should match the set value");
    }

    @Test
    @DisplayName("Should set and get quantity correctly")
    void testSetAndGetQuantity() {
        // Given
        OrderItemEvent event = new OrderItemEvent();
        Integer quantity = 10;

        // When
        event.setQuantity(quantity);

        // Then
        assertEquals(quantity, event.getQuantity(), "Quantity should match the set value");
    }

    @Test
    @DisplayName("Should handle null values in all-args constructor")
    void testAllArgsConstructorWithNulls() {
        // When
        OrderItemEvent event = new OrderItemEvent(null, null, null, null);

        // Then
        assertNull(event.getItemId(), "Item ID should be null");
        assertNull(event.getItemName(), "Item name should be null");
        assertNull(event.getPrice(), "Price should be null");
        assertNull(event.getQuantity(), "Quantity should be null");
    }

    @Test
    @DisplayName("Should handle setting null values via setters")
    void testSetNullValues() {
        // Given
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10"), 5);

        // When
        event.setItemId(null);
        event.setItemName(null);
        event.setPrice(null);
        event.setQuantity(null);

        // Then
        assertNull(event.getItemId(), "Item ID should be null");
        assertNull(event.getItemName(), "Item name should be null");
        assertNull(event.getPrice(), "Price should be null");
        assertNull(event.getQuantity(), "Quantity should be null");
    }

    @Test
    @DisplayName("Should handle empty string for itemName")
    void testEmptyItemName() {
        // Given
        String emptyName = "";

        // When
        OrderItemEvent event = new OrderItemEvent(1L, emptyName, new BigDecimal("10"), 1);

        // Then
        assertEquals(emptyName, event.getItemName(), "Item name should be empty string");
        assertTrue(event.getItemName().isEmpty(), "Item name should be empty");
    }

    @Test
    @DisplayName("Should handle zero price")
    void testZeroPrice() {
        // Given
        BigDecimal zeroPrice = BigDecimal.ZERO;

        // When
        OrderItemEvent event = new OrderItemEvent(1L, "Free Item", zeroPrice, 1);

        // Then
        assertEquals(0, event.getPrice().compareTo(BigDecimal.ZERO),
                "Price should be zero");
    }

    @Test
    @DisplayName("Should handle zero quantity")
    void testZeroQuantity() {
        // Given
        Integer zeroQuantity = 0;

        // When
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10"), zeroQuantity);

        // Then
        assertEquals(0, event.getQuantity(), "Quantity should be zero");
    }

    @Test
    @DisplayName("Should handle negative quantity")
    void testNegativeQuantity() {
        // Given
        Integer negativeQuantity = -5;

        // When
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10"), negativeQuantity);

        // Then
        assertEquals(-5, event.getQuantity(), "Quantity should be negative");
        assertTrue(event.getQuantity() < 0, "Quantity should be less than zero");
    }

    @Test
    @DisplayName("Should handle negative price")
    void testNegativePrice() {
        // Given
        BigDecimal negativePrice = new BigDecimal("-50.00");

        // When
        OrderItemEvent event = new OrderItemEvent(1L, "Item", negativePrice, 1);

        // Then
        assertTrue(event.getPrice().compareTo(BigDecimal.ZERO) < 0,
                "Price should be negative");
    }

    @Test
    @DisplayName("Should handle large itemId")
    void testLargeItemId() {
        // Given
        Long largeId = Long.MAX_VALUE;

        // When
        OrderItemEvent event = new OrderItemEvent(largeId, "Item", new BigDecimal("10"), 1);

        // Then
        assertEquals(Long.MAX_VALUE, event.getItemId(), "Should handle max Long value");
    }

    @Test
    @DisplayName("Should handle large quantity")
    void testLargeQuantity() {
        // Given
        Integer largeQuantity = Integer.MAX_VALUE;

        // When
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10"), largeQuantity);

        // Then
        assertEquals(Integer.MAX_VALUE, event.getQuantity(),
                "Should handle max Integer value");
    }

    @Test
    @DisplayName("Should handle BigDecimal with different scales")
    void testBigDecimalWithDifferentScales() {
        // Given
        BigDecimal price1 = new BigDecimal("10");
        BigDecimal price2 = new BigDecimal("10.00");
        BigDecimal price3 = new BigDecimal("10.999");

        // When
        OrderItemEvent event1 = new OrderItemEvent(1L, "Item1", price1, 1);
        OrderItemEvent event2 = new OrderItemEvent(2L, "Item2", price2, 1);
        OrderItemEvent event3 = new OrderItemEvent(3L, "Item3", price3, 1);

        // Then
        assertEquals(0, event1.getPrice().compareTo(new BigDecimal("10")));
        assertEquals(0, event2.getPrice().compareTo(new BigDecimal("10.00")));
        assertEquals(0, event3.getPrice().compareTo(new BigDecimal("10.999")));
    }

    @Test
    @DisplayName("Should handle very long item name")
    void testVeryLongItemName() {
        // Given
        String longName = "A".repeat(1000);

        // When
        OrderItemEvent event = new OrderItemEvent(1L, longName, new BigDecimal("10"), 1);

        // Then
        assertEquals(1000, event.getItemName().length(), "Item name length should be 1000");
        assertEquals(longName, event.getItemName(), "Item name should match");
    }

    @Test
    @DisplayName("Should handle item name with special characters")
    void testItemNameWithSpecialCharacters() {
        // Given
        String specialName = "Item @#$%^&*()_+-=[]{}|;':\"<>?,./";

        // When
        OrderItemEvent event = new OrderItemEvent(1L, specialName, new BigDecimal("10"), 1);

        // Then
        assertEquals(specialName, event.getItemName(),
                "Item name with special characters should be preserved");
    }

    @Test
    @DisplayName("Should handle item name with unicode characters")
    void testItemNameWithUnicodeCharacters() {
        // Given
        String unicodeName = "商品名称 プロダクト 제품명 🎁";

        // When
        OrderItemEvent event = new OrderItemEvent(1L, unicodeName, new BigDecimal("10"), 1);

        // Then
        assertEquals(unicodeName, event.getItemName(),
                "Item name with unicode characters should be preserved");
    }

    @Test
    @DisplayName("Should test equals with same values")
    void testEqualsWithSameValues() {
        // Given
        OrderItemEvent event1 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);
        OrderItemEvent event2 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        // Then
        assertEquals(event1, event2, "Events with same values should be equal");
    }

    @Test
    @DisplayName("Should test equals with different values")
    void testEqualsWithDifferentValues() {
        // Given
        OrderItemEvent event1 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);
        OrderItemEvent event2 = new OrderItemEvent(2L, "Item", new BigDecimal("10.00"), 5);

        // Then
        assertNotEquals(event1, event2, "Events with different values should not be equal");
    }

    @Test
    @DisplayName("Should test equals with same reference")
    void testEqualsWithSameReference() {
        // Given
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        // Then
        assertEquals(event, event, "Event should equal itself");
    }

    @Test
    @DisplayName("Should test equals with null")
    void testEqualsWithNull() {
        // Given
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        // Then
        assertNotEquals(null, event, "Event should not equal null");
    }

    @Test
    @DisplayName("Should test equals with different class")
    void testEqualsWithDifferentClass() {
        // Given
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);
        String differentClass = "Not an OrderItemEvent";

        // Then
        assertNotEquals(event, differentClass, "Event should not equal different class");
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void testHashCodeConsistency() {
        // Given
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        // When
        int hashCode1 = event.hashCode();
        int hashCode2 = event.hashCode();

        // Then
        assertEquals(hashCode1, hashCode2, "Hash code should be consistent");
    }

    @Test
    @DisplayName("Should test hashCode for equal objects")
    void testHashCodeForEqualObjects() {
        // Given
        OrderItemEvent event1 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);
        OrderItemEvent event2 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        // Then
        assertEquals(event1.hashCode(), event2.hashCode(),
                "Equal objects should have same hash code");
    }

    @Test
    @DisplayName("Should test toString method")
    void testToString() {
        // Given
        OrderItemEvent event = new OrderItemEvent(1L, "Test Item", new BigDecimal("25.50"), 3);

        // When
        String toString = event.toString();

        // Then
        assertNotNull(toString, "toString should not be null");
        assertTrue(toString.contains("1"), "toString should contain itemId");
        assertTrue(toString.contains("Test Item"), "toString should contain itemName");
        assertTrue(toString.contains("25.50"), "toString should contain price");
        assertTrue(toString.contains("3"), "toString should contain quantity");
    }

    @Test
    @DisplayName("Should test toString with null values")
    void testToStringWithNulls() {
        // Given
        OrderItemEvent event = new OrderItemEvent();

        // When
        String toString = event.toString();

        // Then
        assertNotNull(toString, "toString should not be null even with null fields");
        assertTrue(toString.contains("null"), "toString should contain 'null' for null fields");
    }

    @Test
    @DisplayName("Should handle BigDecimal precision")
    void testBigDecimalPrecision() {
        // Given
        BigDecimal precisePrice = new BigDecimal("19.99999999");

        // When
        OrderItemEvent event = new OrderItemEvent(1L, "Item", precisePrice, 1);

        // Then
        assertEquals(precisePrice, event.getPrice(), "Price precision should be preserved");
        assertEquals(0, event.getPrice().compareTo(new BigDecimal("19.99999999")));
    }

    @Test
    @DisplayName("Should allow updating all fields")
    void testUpdateAllFields() {
        // Given
        OrderItemEvent event = new OrderItemEvent(1L, "Original", new BigDecimal("10"), 1);

        // When
        event.setItemId(2L);
        event.setItemName("Updated");
        event.setPrice(new BigDecimal("20"));
        event.setQuantity(2);

        // Then
        assertEquals(2L, event.getItemId(), "Item ID should be updated");
        assertEquals("Updated", event.getItemName(), "Item name should be updated");
        assertEquals(0, event.getPrice().compareTo(new BigDecimal("20")),
                "Price should be updated");
        assertEquals(2, event.getQuantity(), "Quantity should be updated");
    }

    @Test
    @DisplayName("Should handle whitespace in item name")
    void testItemNameWithWhitespace() {
        // Given
        String nameWithWhitespace = "  Item  Name  With  Spaces  ";

        // When
        OrderItemEvent event = new OrderItemEvent(1L, nameWithWhitespace, new BigDecimal("10"), 1);

        // Then
        assertEquals(nameWithWhitespace, event.getItemName(),
                "Item name with whitespace should be preserved");
    }
}