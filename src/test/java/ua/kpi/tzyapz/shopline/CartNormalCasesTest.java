package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести Cart: допустимі значення і нормальні випадки BL-10, BL-20, BL-40
 * (заборонені входи — у CartTest). Очікувані значення обчислено вручну з тексту
 * BL-вимог docs/sprint2/SRS-1.1.md, а не з реалізації.
 */
@Tag("unit")
@DisplayName("Cart — допустимі значення і нормальні випадки")
class CartNormalCasesTest {

    private static final String SKU = "HAM-010";
    private static final String NAME = "Молоток слюсарний";
    private static final BigDecimal PRICE = new BigDecimal("350.00");

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    @DisplayName("BL-40: порожній кошик — subtotal 0.00 і itemCount 0")
    void emptyCartHasZeroSubtotalAndCount() {
        assertAll("Порожній кошик",
                () -> assertEquals(new BigDecimal("0.00"), cart.subtotal(), "subtotal"),
                () -> assertEquals(0, cart.itemCount(), "itemCount"),
                () -> assertTrue(cart.items().isEmpty(), "рядків немає"));
    }

    @ParameterizedTest(name = "BL-10: addItem з qty={0} приймається")
    @ValueSource(ints = {1, 2, 98, 99})
    void addItemWithQtyInsideRangeIsAccepted(int qty) {
        cart.addItem(SKU, NAME, PRICE, qty);

        assertAll("Рядок додано",
                () -> assertEquals(1, cart.items().size(), "кількість рядків"),
                () -> assertEquals(qty, cart.qtyOf(SKU), "qty рядка"),
                () -> assertEquals(qty, cart.itemCount(), "itemCount"));
    }

    @ParameterizedTest(name = "BL-10: ціна {0} зберігається як {1} (HALF_UP)")
    @CsvSource({
            "10.004, 10.00",
            "10.005, 10.01",
            "0.005, 0.01"
    })
    void unitPriceIsRoundedToKopecksHalfUp(String price, String expected) {
        cart.addItem(SKU, NAME, new BigDecimal(price), 1);

        assertEquals(new BigDecimal(expected), cart.subtotal());
    }

    // BUG-018: на v1 повторне додавання замінює кількість рядка (3 замість 5) — падає на v1
    @Test
    @DisplayName("BL-20: повторне додавання 2 + 3 дає один рядок із кількістю 5")
    void reAddingSameSkuIncreasesExistingLine() {
        cart.addItem(SKU, NAME, PRICE, 2);

        cart.addItem(SKU, NAME, PRICE, 3);

        assertAll("Один рядок, кількість 5",
                () -> assertEquals(1, cart.items().size(), "кількість рядків"),
                () -> assertEquals(5, cart.qtyOf(SKU), "qty рядка"),
                () -> assertEquals(new BigDecimal("1750.00"), cart.subtotal(), "subtotal = 350.00 × 5"));
    }

    // BUG-018: на v1 повторне додавання замінює кількість рядка (97 замість 99) — падає на v1
    @Test
    @DisplayName("BL-20: повторне додавання 2 + 97 = 99 (рівно верхня межа) приймається")
    void reAddingUpToMaxQtyIsAccepted() {
        cart.addItem(SKU, NAME, PRICE, 2);

        cart.addItem(SKU, NAME, PRICE, 97);

        assertAll("Рядок на верхній межі",
                () -> assertEquals(1, cart.items().size(), "кількість рядків"),
                () -> assertEquals(99, cart.qtyOf(SKU), "qty рядка"));
    }

    @Test
    @DisplayName("BL-40: subtotal — сума «ціна × кількість» за всіма рядками, itemCount — сума кількостей")
    void subtotalAndItemCountOverSeveralLines() {
        cart.addItem("PLR-020", "Плоскогубці комбіновані", new BigDecimal("199.99"), 3);
        cart.addItem("SCR-040", "Викрутка", new BigDecimal("0.01"), 1);
        cart.addItem("GLV-050", "Рукавиці робочі", new BigDecimal("45.50"), 2);

        // 199.99 × 3 = 599.97; 0.01 × 1 = 0.01; 45.50 × 2 = 91.00; разом 690.98
        assertAll("Підсумки кошика",
                () -> assertEquals(new BigDecimal("690.98"), cart.subtotal(), "subtotal"),
                () -> assertEquals(6, cart.itemCount(), "itemCount"),
                () -> assertEquals(3, cart.items().size(), "кількість рядків"));
    }

    @ParameterizedTest(name = "BL-40: changeQty 5 → {0} приймається")
    @ValueSource(ints = {1, 99})
    void changeQtyToBoundaryValueIsAccepted(int qty) {
        cart.addItem(SKU, NAME, PRICE, 5);

        cart.changeQty(SKU, qty);

        assertAll("Нова кількість",
                () -> assertEquals(qty, cart.qtyOf(SKU), "qty рядка"),
                () -> assertEquals(qty, cart.itemCount(), "itemCount"),
                () -> assertEquals(PRICE.multiply(BigDecimal.valueOf(qty)), cart.subtotal(), "subtotal"));
    }

    @Test
    @DisplayName("BL-40: removeItem видаляє лише вказаний рядок і перераховує підсумки")
    void removeItemDeletesOnlyThatLine() {
        cart.addItem(SKU, NAME, PRICE, 2);
        cart.addItem("PLR-020", "Плоскогубці комбіновані", new BigDecimal("199.99"), 1);

        cart.removeItem(SKU);

        assertAll("Після видалення",
                () -> assertEquals(1, cart.items().size(), "кількість рядків"),
                () -> assertTrue(cart.items().stream().noneMatch(item -> SKU.equals(item.sku())),
                        "рядка " + SKU + " немає"),
                () -> assertEquals(1, cart.itemCount(), "itemCount"),
                () -> assertEquals(new BigDecimal("199.99"), cart.subtotal(), "subtotal"));
    }
}
