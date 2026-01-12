package com.innowise.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItemEvent Tests")
class OrderItemEventTest {

    @Test
    @DisplayName("Should create event with no-args constructor")
    void testNoArgsConstructor() {
        
        OrderItemEvent event = new OrderItemEvent();

        
        assertNotNull(event, "Event should be created");
        assertNull(event.getItemId(), "Item ID should be null");
        assertNull(event.getItemName(), "Item name should be null");
        assertNull(event.getPrice(), "Price should be null");
        assertNull(event.getQuantity(), "Quantity should be null");
    }

    @Test
    @DisplayName("Should create event with all-args constructor")
    void testAllArgsConstructor() {
        
        Long itemId = 100L;
        String itemName = "Test Product";
        BigDecimal price = new BigDecimal("49.99");
        Integer quantity = 5;

        
        OrderItemEvent event = new OrderItemEvent(itemId, itemName, price, quantity);

        
        assertEquals(itemId, event.getItemId(), "Item ID should match");
        assertEquals(itemName, event.getItemName(), "Item name should match");
        assertEquals(price, event.getPrice(), "Price should match");
        assertEquals(quantity, event.getQuantity(), "Quantity should match");
    }

    @Test
    @DisplayName("Should set and get itemId correctly")
    void testSetAndGetItemId() {
        
        OrderItemEvent event = new OrderItemEvent();
        Long itemId = 200L;

        
        event.setItemId(itemId);

        
        assertEquals(itemId, event.getItemId(), "Item ID should match the set value");
    }

    @Test
    @DisplayName("Should set and get itemName correctly")
    void testSetAndGetItemName() {
        
        OrderItemEvent event = new OrderItemEvent();
        String itemName = "Laptop Computer";

        
        event.setItemName(itemName);

        
        assertEquals(itemName, event.getItemName(), "Item name should match the set value");
    }

    @Test
    @DisplayName("Should set and get price correctly")
    void testSetAndGetPrice() {
        
        OrderItemEvent event = new OrderItemEvent();
        BigDecimal price = new BigDecimal("999.99");

        
        event.setPrice(price);

        
        assertEquals(price, event.getPrice(), "Price should match the set value");
    }

    @Test
    @DisplayName("Should set and get quantity correctly")
    void testSetAndGetQuantity() {
        
        OrderItemEvent event = new OrderItemEvent();
        Integer quantity = 10;

        
        event.setQuantity(quantity);

        
        assertEquals(quantity, event.getQuantity(), "Quantity should match the set value");
    }

    @Test
    @DisplayName("Should handle null values in all-args constructor")
    void testAllArgsConstructorWithNulls() {
        
        OrderItemEvent event = new OrderItemEvent(null, null, null, null);

        
        assertNull(event.getItemId(), "Item ID should be null");
        assertNull(event.getItemName(), "Item name should be null");
        assertNull(event.getPrice(), "Price should be null");
        assertNull(event.getQuantity(), "Quantity should be null");
    }

    @Test
    @DisplayName("Should handle setting null values via setters")
    void testSetNullValues() {
        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10"), 5);

        
        event.setItemId(null);
        event.setItemName(null);
        event.setPrice(null);
        event.setQuantity(null);

        
        assertNull(event.getItemId(), "Item ID should be null");
        assertNull(event.getItemName(), "Item name should be null");
        assertNull(event.getPrice(), "Price should be null");
        assertNull(event.getQuantity(), "Quantity should be null");
    }

    @Test
    @DisplayName("Should handle empty string for itemName")
    void testEmptyItemName() {
        
        String emptyName = "";

        
        OrderItemEvent event = new OrderItemEvent(1L, emptyName, new BigDecimal("10"), 1);

        
        assertEquals(emptyName, event.getItemName(), "Item name should be empty string");
        assertTrue(event.getItemName().isEmpty(), "Item name should be empty");
    }

    @Test
    @DisplayName("Should handle zero price")
    void testZeroPrice() {
        
        BigDecimal zeroPrice = BigDecimal.ZERO;

        
        OrderItemEvent event = new OrderItemEvent(1L, "Free Item", zeroPrice, 1);

        
        assertEquals(0, event.getPrice().compareTo(BigDecimal.ZERO),
                "Price should be zero");
    }

    @Test
    @DisplayName("Should handle zero quantity")
    void testZeroQuantity() {
        
        Integer zeroQuantity = 0;

        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10"), zeroQuantity);

        
        assertEquals(0, event.getQuantity(), "Quantity should be zero");
    }

    @Test
    @DisplayName("Should handle negative quantity")
    void testNegativeQuantity() {
        
        Integer negativeQuantity = -5;

        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10"), negativeQuantity);

        
        assertEquals(-5, event.getQuantity(), "Quantity should be negative");
        assertTrue(event.getQuantity() < 0, "Quantity should be less than zero");
    }

    @Test
    @DisplayName("Should handle negative price")
    void testNegativePrice() {
        
        BigDecimal negativePrice = new BigDecimal("-50.00");

        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", negativePrice, 1);

        
        assertTrue(event.getPrice().compareTo(BigDecimal.ZERO) < 0,
                "Price should be negative");
    }

    @Test
    @DisplayName("Should handle large itemId")
    void testLargeItemId() {
        
        Long largeId = Long.MAX_VALUE;

        
        OrderItemEvent event = new OrderItemEvent(largeId, "Item", new BigDecimal("10"), 1);

        
        assertEquals(Long.MAX_VALUE, event.getItemId(), "Should handle max Long value");
    }

    @Test
    @DisplayName("Should handle large quantity")
    void testLargeQuantity() {
        
        Integer largeQuantity = Integer.MAX_VALUE;

        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10"), largeQuantity);

        
        assertEquals(Integer.MAX_VALUE, event.getQuantity(),
                "Should handle max Integer value");
    }

    @Test
    @DisplayName("Should handle BigDecimal with different scales")
    void testBigDecimalWithDifferentScales() {
        
        BigDecimal price1 = new BigDecimal("10");
        BigDecimal price2 = new BigDecimal("10.00");
        BigDecimal price3 = new BigDecimal("10.999");

        
        OrderItemEvent event1 = new OrderItemEvent(1L, "Item1", price1, 1);
        OrderItemEvent event2 = new OrderItemEvent(2L, "Item2", price2, 1);
        OrderItemEvent event3 = new OrderItemEvent(3L, "Item3", price3, 1);

        
        assertEquals(0, event1.getPrice().compareTo(new BigDecimal("10")));
        assertEquals(0, event2.getPrice().compareTo(new BigDecimal("10.00")));
        assertEquals(0, event3.getPrice().compareTo(new BigDecimal("10.999")));
    }

    @Test
    @DisplayName("Should handle very long item name")
    void testVeryLongItemName() {
        
        String longName = "A".repeat(1000);

        
        OrderItemEvent event = new OrderItemEvent(1L, longName, new BigDecimal("10"), 1);

        
        assertEquals(1000, event.getItemName().length(), "Item name length should be 1000");
        assertEquals(longName, event.getItemName(), "Item name should match");
    }

    @Test
    @DisplayName("Should handle item name with special characters")
    void testItemNameWithSpecialCharacters() {
        
        String specialName = "Item @#$%^&*()_+-=[]{}|;':\"<>?,./";

        
        OrderItemEvent event = new OrderItemEvent(1L, specialName, new BigDecimal("10"), 1);

        
        assertEquals(specialName, event.getItemName(),
                "Item name with special characters should be preserved");
    }

    @Test
    @DisplayName("Should handle item name with unicode characters")
    void testItemNameWithUnicodeCharacters() {
        
        String unicodeName = "商品名称 プロダクト 제품명 🎁";

        
        OrderItemEvent event = new OrderItemEvent(1L, unicodeName, new BigDecimal("10"), 1);

        
        assertEquals(unicodeName, event.getItemName(),
                "Item name with unicode characters should be preserved");
    }

    @Test
    @DisplayName("Should test equals with same values")
    void testEqualsWithSameValues() {
        
        OrderItemEvent event1 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);
        OrderItemEvent event2 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        
        assertEquals(event1, event2, "Events with same values should be equal");
    }

    @Test
    @DisplayName("Should test equals with different values")
    void testEqualsWithDifferentValues() {
        
        OrderItemEvent event1 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);
        OrderItemEvent event2 = new OrderItemEvent(2L, "Item", new BigDecimal("10.00"), 5);

        
        assertNotEquals(event1, event2, "Events with different values should not be equal");
    }

    @Test
    @DisplayName("Should test equals with same reference")
    void testEqualsWithSameReference() {
        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        
        assertEquals(event, event, "Event should equal itself");
    }

    @Test
    @DisplayName("Should test equals with null")
    void testEqualsWithNull() {
        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        
        assertNotEquals(null, event, "Event should not equal null");
    }

    @Test
    @DisplayName("Should test equals with different class")
    void testEqualsWithDifferentClass() {
        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);
        String differentClass = "Not an OrderItemEvent";

        
        assertNotEquals(event, differentClass, "Event should not equal different class");
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void testHashCodeConsistency() {
        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        
        int hashCode1 = event.hashCode();
        int hashCode2 = event.hashCode();

        
        assertEquals(hashCode1, hashCode2, "Hash code should be consistent");
    }

    @Test
    @DisplayName("Should test hashCode for equal objects")
    void testHashCodeForEqualObjects() {
        
        OrderItemEvent event1 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);
        OrderItemEvent event2 = new OrderItemEvent(1L, "Item", new BigDecimal("10.00"), 5);

        
        assertEquals(event1.hashCode(), event2.hashCode(),
                "Equal objects should have same hash code");
    }

    @Test
    @DisplayName("Should test toString method")
    void testToString() {
        
        OrderItemEvent event = new OrderItemEvent(1L, "Test Item", new BigDecimal("25.50"), 3);

        
        String toString = event.toString();

        
        assertNotNull(toString, "toString should not be null");
        assertTrue(toString.contains("1"), "toString should contain itemId");
        assertTrue(toString.contains("Test Item"), "toString should contain itemName");
        assertTrue(toString.contains("25.50"), "toString should contain price");
        assertTrue(toString.contains("3"), "toString should contain quantity");
    }

    @Test
    @DisplayName("Should test toString with null values")
    void testToStringWithNulls() {
        
        OrderItemEvent event = new OrderItemEvent();

        
        String toString = event.toString();

        
        assertNotNull(toString, "toString should not be null even with null fields");
        assertTrue(toString.contains("null"), "toString should contain 'null' for null fields");
    }

    @Test
    @DisplayName("Should handle BigDecimal precision")
    void testBigDecimalPrecision() {
        
        BigDecimal precisePrice = new BigDecimal("19.99999999");

        
        OrderItemEvent event = new OrderItemEvent(1L, "Item", precisePrice, 1);

        
        assertEquals(precisePrice, event.getPrice(), "Price precision should be preserved");
        assertEquals(0, event.getPrice().compareTo(new BigDecimal("19.99999999")));
    }

    @Test
    @DisplayName("Should allow updating all fields")
    void testUpdateAllFields() {
        
        OrderItemEvent event = new OrderItemEvent(1L, "Original", new BigDecimal("10"), 1);

        
        event.setItemId(2L);
        event.setItemName("Updated");
        event.setPrice(new BigDecimal("20"));
        event.setQuantity(2);

        
        assertEquals(2L, event.getItemId(), "Item ID should be updated");
        assertEquals("Updated", event.getItemName(), "Item name should be updated");
        assertEquals(0, event.getPrice().compareTo(new BigDecimal("20")),
                "Price should be updated");
        assertEquals(2, event.getQuantity(), "Quantity should be updated");
    }

    @Test
    @DisplayName("Should handle whitespace in item name")
    void testItemNameWithWhitespace() {
        
        String nameWithWhitespace = "  Item  Name  With  Spaces  ";

        
        OrderItemEvent event = new OrderItemEvent(1L, nameWithWhitespace, new BigDecimal("10"), 1);

        
        assertEquals(nameWithWhitespace, event.getItemName(),
                "Item name with whitespace should be preserved");
    }
}