package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Зразок стилю юніт-тестів для модуля «ШопЛайн»: один клас тестів на клас модуля,
 * назви тестів посилаються на ідентифікатор правила BL-xx зі специфікації.
 */
@DisplayName("ШопЛайн · зразки юніт-тестів модуля")
class ExampleTest {

    private Cart cart;
    private DiscountCalculator discounts;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        discounts = new DiscountCalculator();
    }

    @Test
    @DisplayName("BL-40: subtotal та itemCount для двох різних товарів")
    void subtotalAndCountOfTwoDifferentItems() {
        cart.addItem("HAM-010", "Молоток слюсарний", new BigDecimal("350.00"), 2);
        cart.addItem("PLR-020", "Плоскогубці комбіновані", new BigDecimal("199.99"), 1);
        assertAll(
            () -> assertEquals(new BigDecimal("899.99"), cart.subtotal()),
            () -> assertEquals(3, cart.itemCount())
        );
    }

    @Test
    @DisplayName("BL-50/BL-60: 3000 грн → знижка 5 % = 150.00 грн, доставка безкоштовна")
    void fivePercentTierAndFreeShipping() {
        BigDecimal subtotal = new BigDecimal("3000.00");
        BigDecimal afterDiscount = subtotal.subtract(discounts.volumeDiscount(subtotal));
        assertAll(
            () -> assertEquals(5, discounts.volumeDiscountPercent(subtotal)),
            () -> assertEquals(new BigDecimal("150.00"), discounts.volumeDiscount(subtotal)),
            () -> assertEquals(new BigDecimal("0.00"), discounts.shippingFee(afterDiscount))
        );
    }
}
