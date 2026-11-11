package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юніт-тести PromoCodeValidator: BL-70 (придатність промокоду — строк дії, одноразовість,
 * межі значення знижки) і BL-80 (сума знижки). Перевірка формату коду (null, довжина,
 * символи) винесена в окремий тест-клас.
 *
 * Очікувані значення взято з тексту BL-70/BL-80 (docs/sprint2/SRS-1.1.md), а не з реалізації.
 * Тест, що падає на v1, відповідає звіту про дефект, ID якого вказано в коментарі до тесту.
 */
@Tag("unit")
@DisplayName("PromoCodeValidator · BL-70 (придатність), BL-80 (сума знижки)")
class PromoCodeValidatorRulesTest {

    private static final LocalDate EXPIRES_ON = LocalDate.of(2026, 12, 31);
    private static final BigDecimal SUBTOTAL = new BigDecimal("500.00");

    private final PromoCodeValidator validator = new PromoCodeValidator();

    private static PromoCode percentCode(String percent, boolean singleUse, boolean used) {
        return new PromoCode("WINTER26", PromoCode.Type.PERCENT, new BigDecimal(percent),
                EXPIRES_ON, singleUse, used);
    }

    private static PromoCode fixedCode(String amount) {
        return new PromoCode("WINTER26", PromoCode.Type.FIXED, new BigDecimal(amount),
                EXPIRES_ON, false, false);
    }

    // ---------- BL-70: придатність ----------

    @Test
    @DisplayName("BL-70: код, що задовольняє всі умови, придатний; причина відмови порожня")
    void validCodeIsAccepted() {
        PromoCode promo = percentCode("10", true, false);

        PromoCodeValidator.ValidationResult result =
                validator.validate(promo, EXPIRES_ON.minusDays(30), SUBTOTAL);

        assertAll("Придатний код",
                () -> assertTrue(result.valid(), "ознака придатності"),
                () -> assertEquals("", result.reason(), "причина відмови"));
    }

    // BUG-019: на v1 у день закінчення (daysAfterExpiry = 0) код відхиляється — червоний рядок [2]
    @ParameterizedTest(name = "BL-70: today = дата закінчення {0} дн. → придатний: {1}")
    @DisplayName("BL-70: дата закінчення дії включно (межа з обох боків)")
    @CsvSource({
            "-1, true",
            "0, true",
            "1, false"
    })
    void expiryDateIsInclusive(int daysAfterExpiry, boolean expectedValid) {
        PromoCode promo = percentCode("10", false, false);
        LocalDate today = EXPIRES_ON.plusDays(daysAfterExpiry);

        PromoCodeValidator.ValidationResult result = validator.validate(promo, today, SUBTOTAL);

        assertAll("Строк дії",
                () -> assertEquals(expectedValid, result.valid(), "ознака придатності"),
                () -> assertEquals(expectedValid, result.reason().isEmpty(),
                        "причина відмови порожня лише для придатного коду"));
    }

    // BUG-020: на v1 ознака «використано» ігнорується — червоний рядок [2]
    @ParameterizedTest(name = "BL-70: singleUse={0}, used={1} → придатний: {2}")
    @DisplayName("BL-70: одноразовий код має бути ще не використаним")
    @CsvSource({
            "true, false, true",
            "true, true, false",
            "false, false, true",
            "false, true, true"
    })
    void singleUseCodeMustNotBeUsed(boolean singleUse, boolean used, boolean expectedValid) {
        PromoCode promo = percentCode("10", singleUse, used);

        PromoCodeValidator.ValidationResult result =
                validator.validate(promo, EXPIRES_ON.minusDays(1), SUBTOTAL);

        assertEquals(expectedValid, result.valid());
    }

    @ParameterizedTest(name = "BL-70: відсоток {0} → придатний: {1}")
    @DisplayName("BL-70: відсоток знижки від 1 до 50 включно (межі з обох боків)")
    @CsvSource({
            "0.99, false",
            "1, true",
            "50, true",
            "50.01, false"
    })
    void percentValueBoundaries(String percent, boolean expectedValid) {
        PromoCode promo = percentCode(percent, false, false);

        PromoCodeValidator.ValidationResult result =
                validator.validate(promo, EXPIRES_ON.minusDays(1), SUBTOTAL);

        assertEquals(expectedValid, result.valid());
    }

    @ParameterizedTest(name = "BL-70: фіксована сума {0} при підсумку 500.00 → придатний: {1}")
    @DisplayName("BL-70: фіксована сума більша за 0 і не перевищує підсумок (межі з обох боків)")
    @CsvSource({
            "0, false",
            "0.01, true",
            "500.00, true",
            "500.01, false"
    })
    void fixedValueBoundaries(String amount, boolean expectedValid) {
        PromoCode promo = fixedCode(amount);

        PromoCodeValidator.ValidationResult result =
                validator.validate(promo, EXPIRES_ON.minusDays(1), SUBTOTAL);

        assertEquals(expectedValid, result.valid());
    }

    @Test
    @DisplayName("BL-70: за кількох порушень причина «Термін дії минув» має пріоритет над недопустимим значенням знижки")
    void expiredReasonHasPriorityOverInvalidDiscountValue() {
        LocalDate afterExpiry = EXPIRES_ON.plusDays(1);
        PromoCodeValidator.ValidationResult expiredOnly =
                validator.validate(percentCode("10", false, false), afterExpiry, SUBTOTAL);
        PromoCodeValidator.ValidationResult expiredAndInvalidValue =
                validator.validate(percentCode("51", false, false), afterExpiry, SUBTOTAL);

        assertAll("Пріоритет причин відмови",
                () -> assertFalse(expiredOnly.valid(), "код із минулим строком непридатний"),
                () -> assertFalse(expiredOnly.reason().isEmpty(), "причина для строку дії не порожня"),
                () -> assertEquals(expiredOnly.reason(), expiredAndInvalidValue.reason(),
                        "причина при двох порушеннях"));
    }

    // ---------- BL-80: сума знижки ----------

    @Test
    @DisplayName("BL-80: знижка у відсотках округлюється HALF_UP до копійок (1 % від 1000.50 = 10.005 → 10.01)")
    void percentDiscountRoundsHalfUp() {
        PromoCode promo = percentCode("1", false, false);
        BigDecimal subtotal = new BigDecimal("1000.50");

        BigDecimal discount = validator.apply(promo, EXPIRES_ON.minusDays(1), subtotal);

        assertEquals(new BigDecimal("10.01"), discount);
    }

    @Test
    @DisplayName("BL-80: фіксована знижка дорівнює значенню коду")
    void fixedDiscountEqualsCodeValue() {
        PromoCode promo = fixedCode("150.00");

        BigDecimal discount = validator.apply(promo, EXPIRES_ON.minusDays(1), SUBTOTAL);

        assertEquals(new BigDecimal("150.00"), discount);
    }

    @Test
    @DisplayName("BL-80: прострочений код — IllegalArgumentException із причиною відмови")
    void applyRejectsExpiredCode() {
        PromoCode promo = percentCode("10", false, false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> validator.apply(promo, EXPIRES_ON.plusDays(1), SUBTOTAL));

        assertFalse(thrown.getMessage() == null || thrown.getMessage().isBlank(),
                "виняток містить причину відмови");
    }

    // BUG-020: на v1 використаний одноразовий код застосовується без винятку
    @Test
    @DisplayName("BL-80: використаний одноразовий код — IllegalArgumentException")
    void applyRejectsUsedSingleUseCode() {
        PromoCode promo = percentCode("10", true, true);

        assertThrows(IllegalArgumentException.class,
                () -> validator.apply(promo, EXPIRES_ON.minusDays(1), SUBTOTAL));
    }
}
