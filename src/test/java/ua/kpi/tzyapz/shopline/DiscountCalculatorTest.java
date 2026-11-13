package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Юніт-тести DiscountCalculator (TTS-50 / S4-06).
 * Правила: BL-50 (знижка за обсяг) і BL-60 (вартість доставки).
 *
 * Очікувані значення пораховані за текстом правил BL-50/BL-60
 * (docs/sprint2/SRS-1.1.md), а НЕ підглянуті в самій реалізації.
 * Якщо якийсь тест після запуску виявиться RED — це знайдений
 * дефект модуля, а не помилка тесту. Такі випадки оформлюються
 * окремим GitHub Issue з міткою module (задача TTS-56).
 */
@DisplayName("DiscountCalculator · BL-50 (знижка за обсяг), BL-60 (доставка)")
class DiscountCalculatorTest {

    private final DiscountCalculator discounts = new DiscountCalculator();

    // ---------- BL-50: пороги знижки за обсяг ----------
    // Правило: 0 % (< 2000), 5 % (від 2000 включно), 10 % (від 5000 включно),
    // 15 % (від 10000 включно). Застосовується лише найвищий досягнутий рівень.

    // BUG-015: subtotal=5000.00 → volumeDiscountPercent повертає 5% замість очікуваних 10%
    @ParameterizedTest(name = "subtotal={0} → очікується {1}%")
    @DisplayName("BL-50: межі тарифів знижки (volumeDiscountPercent)")
    @CsvSource({
        "1999.99, 0",
        "2000.00, 5",
        "4999.99, 5",
        "5000.00, 10",
        "9999.99, 10",
        "10000.00, 15"
    })
    void volumeDiscountPercent_boundaries(String subtotal, int expectedPercent) {
        int actual = discounts.volumeDiscountPercent(new BigDecimal(subtotal));
        assertEquals(expectedPercent, actual);
    }

    // BUG-016: volumeDiscount округлює вниз (FLOOR) замість HALF_UP; 150.005 повертає 150.00 замість 150.01
    @Test
    @DisplayName("BL-50: сума знижки округлюється HALF_UP до копійок")
    void volumeDiscount_roundsHalfUp() {
        // 3000.10 у тарифі 5%: 3000.10 * 5 / 100 = 150.005 → HALF_UP до 150.01
        BigDecimal subtotal = new BigDecimal("3000.10");
        BigDecimal expected = new BigDecimal("150.01");

        assertEquals(expected, discounts.volumeDiscount(subtotal));
    }

    // ---------- BL-60: вартість доставки ----------
    // Правило: безкоштовна доставка, якщо сума після знижки >= 1500 грн, інакше 120.00 грн.

    @ParameterizedTest(name = "subtotalAfterDiscount={0} → очікується {1}")
    @DisplayName("BL-60: межа безкоштовної доставки (shippingFee)")
    @CsvSource({
        "1499.99, 120.00",
        "1500.00, 0.00",
        "1500.01, 0.00"
    })
    void shippingFee_boundaries(String subtotalAfterDiscount, String expected) {
        BigDecimal actual = discounts.shippingFee(new BigDecimal(subtotalAfterDiscount));
        assertEquals(new BigDecimal(expected), actual);
    }

    // ---------- Заборонені входи ----------

    @Test
    @DisplayName("BL-50/60: від'ємна сума — IllegalArgumentException")
    void negativeSubtotal_throwsException() {
        BigDecimal negative = new BigDecimal("-0.01");

        assertThrows(IllegalArgumentException.class,
            () -> discounts.volumeDiscountPercent(negative));
    }
}