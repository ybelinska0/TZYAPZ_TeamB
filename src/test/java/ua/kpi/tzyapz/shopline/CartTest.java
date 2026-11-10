package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@DisplayName("Cart — заборонені входи і винятки")
class CartTest {

    private static final String SKU = "HAM-010";
    private static final BigDecimal SUBTOTAL_BEFORE = new BigDecimal("700.00");
    private static final int COUNT_BEFORE = 2;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.addItem(SKU, "Молоток слюсарний", new BigDecimal("350.00"), 2);
    }

    private void assertCartUnchanged() {
        assertAll("Кошик не змінився",
                () -> assertEquals(1, cart.items().size(), "кількість рядків"),
                () -> assertEquals(COUNT_BEFORE, cart.itemCount(), "itemCount"),
                () -> assertEquals(SUBTOTAL_BEFORE, cart.subtotal(), "subtotal"),
                () -> assertEquals(COUNT_BEFORE, cart.qtyOf(SKU), "qty рядка " + SKU)
        );
    }

    // BUG-017: верхня межа qty 100 замість 99 — падає на v1 при qty=100
    @ParameterizedTest(name = "BL-10: addItem з qty={0} → IllegalArgumentException")
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 100, 101})
    void addItemWithQtyOutOfRangeIsRejected(int qty) {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("SAW-030", "Ножівка", new BigDecimal("250.00"), qty));
        assertCartUnchanged();
    }

    @ParameterizedTest(name = "BL-10: addItem з ціною {0} → IllegalArgumentException")
    @NullSource
    @ValueSource(strings = {"0", "0.00", "-0.01", "-350.00"})
    void addItemWithNonPositivePriceIsRejected(String price) {
        BigDecimal unitPrice = price == null ? null : new BigDecimal(price);
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("SAW-030", "Ножівка", unitPrice, 1));
        assertCartUnchanged();
    }

    @ParameterizedTest(name = "BL-10: addItem з артикулом \"{0}\" → IllegalArgumentException")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void addItemWithBlankSkuIsRejected(String sku) {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem(sku, "Ножівка", new BigDecimal("250.00"), 1));
        assertCartUnchanged();
    }

    // BUG-018: повторне додавання замінює рядок замість збільшення qty з перевіркою ≤ 99 — падає на v1
    @Test
    @DisplayName("BL-20: повторне додавання, що дає 2 + 98 = 100 > 99 → IllegalArgumentException, кошик не змінюється")
    void reAddingSameSkuAboveMaxQtyIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem(SKU, "Молоток слюсарний", new BigDecimal("350.00"), 98));
        assertCartUnchanged();
    }

    // BUG-017: верхня межа qty 100 замість 99 — падає на v1 при qty=100
    @ParameterizedTest(name = "BL-40: changeQty з qty={0} → IllegalArgumentException")
    @ValueSource(ints = {-1, 0, 100})
    void changeQtyOutOfRangeIsRejected(int qty) {
        assertThrows(IllegalArgumentException.class, () -> cart.changeQty(SKU, qty));
        assertCartUnchanged();
    }

    @Test
    @DisplayName("BL-40: removeItem для відсутнього артикула → NoSuchElementException")
    void removeMissingSkuThrows() {
        assertThrows(NoSuchElementException.class, () -> cart.removeItem("NOPE-999"));
        assertCartUnchanged();
    }

    @Test
    @DisplayName("BL-40: changeQty для відсутнього артикула → NoSuchElementException")
    void changeQtyOfMissingSkuThrows() {
        assertThrows(NoSuchElementException.class, () -> cart.changeQty("NOPE-999", 5));
        assertCartUnchanged();
    }
}
