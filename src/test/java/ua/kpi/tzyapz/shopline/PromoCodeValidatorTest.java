package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести PromoCodeValidator: формат коду за BL-70 — довжина 6–10 символів, лише A–Z і 0–9,
 * null → «Невірний формат», і ця причина має найвищий пріоритет.
 * Строк дії, одноразовість, межі значення і сума знижки — у PromoCodeValidatorRulesTest
 * та PromoCodeValidatorReasonsTest.
 *
 * Очікувані значення взято з тексту BL-70 (docs/sprint2/SRS-1.1.md), а не з реалізації.
 */
@Tag("unit")
@DisplayName("PromoCodeValidator · BL-70 (формат коду)")
class PromoCodeValidatorTest {

    private static final LocalDate EXPIRES_ON = LocalDate.of(2026, 12, 31);
    private static final LocalDate TODAY = EXPIRES_ON.minusDays(1);
    private static final BigDecimal SUBTOTAL = new BigDecimal("500.00");

    private PromoCodeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PromoCodeValidator();
    }

    /** Код, у якого всі умови BL-70, крім формату, виконано: строк не минув, багаторазовий, 10 %. */
    private static PromoCode codeWithText(String text) {
        return new PromoCode(text, PromoCode.Type.PERCENT, new BigDecimal("10"), EXPIRES_ON, false, false);
    }

    @ParameterizedTest(name = "BL-70: код \"{0}\" ({1} симв.) → придатний: {2}")
    @DisplayName("BL-70: довжина коду від 6 до 10 символів включно (межі з обох боків)")
    @CsvSource({
            "ABCDE,       5,  false",
            "ABCDE1,      6,  true",
            "ABCDEFGH10,  10, true",
            "ABCDEFGHI11, 11, false"
    })
    void codeLengthBoundaries(String text, int length, boolean expectedValid) {
        assertEquals(length, text.length(), "довжина тестових даних");

        PromoCodeValidator.ValidationResult result = validator.validate(codeWithText(text), TODAY, SUBTOTAL);

        assertAll("Довжина коду",
                () -> assertEquals(expectedValid, result.valid(), "ознака придатності"),
                () -> assertEquals(expectedValid, result.reason().isEmpty(),
                        "причина відмови порожня лише для придатного коду"));
    }

    @ParameterizedTest(name = "BL-70: код \"{0}\" з допустимих символів приймається")
    @DisplayName("BL-70: великі латинські літери і цифри — допустимі символи")
    @ValueSource(strings = {"WINTER26", "SPRINGSALE", "123456"})
    void codeWithAllowedCharactersIsAccepted(String text) {
        PromoCodeValidator.ValidationResult result = validator.validate(codeWithText(text), TODAY, SUBTOTAL);

        assertAll("Допустимий формат",
                () -> assertTrue(result.valid(), "ознака придатності"),
                () -> assertEquals("", result.reason(), "причина відмови"));
    }

    @ParameterizedTest(name = "BL-70: код \"{0}\" — невірний формат")
    @DisplayName("BL-70: null, малі літери, кирилиця, спецсимвол і пробіл — невірний формат")
    @NullSource
    @ValueSource(strings = {"winter26", "ЗИМА2026", "SAVE-20", "SAVE 20"})
    void codeWithInvalidFormatIsRejected(String text) {
        PromoCodeValidator.ValidationResult result = validator.validate(codeWithText(text), TODAY, SUBTOTAL);

        assertAll("Невірний формат",
                () -> assertFalse(result.valid(), "ознака придатності"),
                () -> assertFalse(result.reason() == null || result.reason().isBlank(), "причина відмови"));
    }

    @Test
    @DisplayName("BL-70: за кількох порушень першою повертається причина «Невірний формат»")
    void invalidFormatReasonHasPriorityOverOtherViolations() {
        String formatOnly = validator.validate(codeWithText("ABC"), TODAY, SUBTOTAL).reason();
        PromoCode everythingWrong = new PromoCode("ABC", PromoCode.Type.PERCENT, new BigDecimal("51"),
                EXPIRES_ON, true, true);

        PromoCodeValidator.ValidationResult result =
                validator.validate(everythingWrong, EXPIRES_ON.plusDays(1), SUBTOTAL);

        assertAll("Пріоритет «Невірний формат»",
                () -> assertFalse(result.valid(), "ознака придатності"),
                () -> assertEquals(formatOnly, result.reason(),
                        "причина та сама, що й для коду лише з невірним форматом"));
    }
}
