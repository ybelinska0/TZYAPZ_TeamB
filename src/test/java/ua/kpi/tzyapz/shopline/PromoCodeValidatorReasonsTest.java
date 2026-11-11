package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести PromoCodeValidator, ч. 2: причини відмови та їх порядок (BL-70), сума знижки для
 * придатних кодів і відмова apply для недопустимого значення (BL-80).
 * Формат коду — у тест-класі ТА; строк дії, одноразовість і межі значення — у PromoCodeValidatorRulesTest.
 *
 * Очікувані значення взято з тексту BL-70/BL-80 (docs/sprint2/SRS-1.1.md), а не з реалізації.
 * Точні тексти причин у SRS 1.1 позначено як «пропоноване уточнення, потребує підтвердження
 * замовником», тому тести порівнюють причини між собою, а не з конкретним текстом.
 */
@Tag("unit")
@DisplayName("PromoCodeValidator · BL-70 (причини відмови), BL-80 (сума знижки)")
class PromoCodeValidatorReasonsTest {

    private static final LocalDate EXPIRES_ON = LocalDate.of(2026, 12, 31);
    private static final LocalDate BEFORE_EXPIRY = EXPIRES_ON.minusDays(1);
    private static final LocalDate AFTER_EXPIRY = EXPIRES_ON.plusDays(1);
    private static final BigDecimal SUBTOTAL = new BigDecimal("500.00");

    private final PromoCodeValidator validator = new PromoCodeValidator();

    private static PromoCode code(PromoCode.Type type, String value, boolean singleUse, boolean used) {
        return new PromoCode("SPRING26", type, new BigDecimal(value), EXPIRES_ON, singleUse, used);
    }

    private String reasonOf(PromoCode promo, LocalDate today) {
        return validator.validate(promo, today, SUBTOTAL).reason();
    }

    @ParameterizedTest(name = "BL-70: {0} {1}, today після строку={2} → непридатний, причина не порожня")
    @DisplayName("BL-70: непридатний код завжди має непорожню причину відмови")
    @CsvSource({
            "PERCENT, 10,     true",
            "PERCENT, 51,     false",
            "FIXED,   500.01, false"
    })
    void invalidCodeHasNonEmptyReason(PromoCode.Type type, String value, boolean expired) {
        PromoCode promo = code(type, value, false, false);
        LocalDate today = expired ? AFTER_EXPIRY : BEFORE_EXPIRY;

        PromoCodeValidator.ValidationResult result = validator.validate(promo, today, SUBTOTAL);

        assertAll("Відмова з причиною",
                () -> assertFalse(result.valid(), "ознака придатності"),
                () -> assertFalse(result.reason() == null || result.reason().isBlank(), "причина відмови"));
    }

    @Test
    @DisplayName("BL-70: строк минув і одноразовий код використано → причина та сама, що для «Термін дії минув»")
    void expiredReasonHasPriorityOverUsedCode() {
        String expiredOnly = reasonOf(code(PromoCode.Type.PERCENT, "10", false, false), AFTER_EXPIRY);

        String expiredAndUsed = reasonOf(code(PromoCode.Type.PERCENT, "10", true, true), AFTER_EXPIRY);

        assertEquals(expiredOnly, expiredAndUsed);
    }

    // BUG-020: на v1 ознака «використано» ігнорується, тому повертається причина про значення знижки — падає на v1
    @Test
    @DisplayName("BL-70: код використано і відсоток 51 → причина та сама, що для «Код уже використано»")
    void usedCodeReasonHasPriorityOverInvalidValue() {
        String usedOnly = reasonOf(code(PromoCode.Type.PERCENT, "10", true, true), BEFORE_EXPIRY);

        String usedAndInvalidValue = reasonOf(code(PromoCode.Type.PERCENT, "51", true, true), BEFORE_EXPIRY);

        assertAll("Пріоритет «Код уже використано»",
                () -> assertFalse(usedOnly.isEmpty(), "використаний одноразовий код має причину відмови"),
                () -> assertEquals(usedOnly, usedAndInvalidValue, "причина при двох порушеннях"));
    }

    @ParameterizedTest(name = "BL-80: {0} {1} від {2} → знижка {3}")
    @DisplayName("BL-80: сума знижки для придатного коду (відсоток з HALF_UP, фіксована — значення коду)")
    @CsvSource({
            "PERCENT, 15,     1234.56, 185.18",
            "PERCENT, 50,     0.01,    0.01",
            "PERCENT, 1,      2000.00, 20.00",
            "FIXED,   500.00, 500.00,  500.00"
    })
    void applyReturnsDiscountForValidCode(PromoCode.Type type, String value, String subtotal, String expected) {
        PromoCode promo = code(type, value, false, false);

        BigDecimal discount = validator.apply(promo, BEFORE_EXPIRY, new BigDecimal(subtotal));

        assertEquals(new BigDecimal(expected), discount);
    }

    @Test
    @DisplayName("BL-80: фіксована сума більша за підсумок — IllegalArgumentException із причиною відмови validate")
    void applyRejectsFixedValueAboveSubtotal() {
        PromoCode promo = code(PromoCode.Type.FIXED, "500.01", false, false);
        String expectedReason = reasonOf(promo, BEFORE_EXPIRY);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> validator.apply(promo, BEFORE_EXPIRY, SUBTOTAL));

        assertEquals(expectedReason, thrown.getMessage());
    }
}
