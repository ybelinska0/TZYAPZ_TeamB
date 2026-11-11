package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@DisplayName("PromoCodeValidator - валідація формату та бізнес-правил (BL-70)")
class PromoCodeValidatorTest {

    private PromoCodeValidator validator;

    @BeforeEach
    void setup() {
        validator = new PromoCodeValidator();
    }

    @ParameterizedTest(name = "BL-70: коректна довжина коду 6-10 символів ({0}) проходить валідацію")
    @ValueSource(strings = {"PROMO16", "WINTER2026", "SPRING2026"})
    void validLengthCodePasses(String codeStr) {
        PromoCode promo = new PromoCode(codeStr, new BigDecimal("10.00"));
        assertDoesNotThrow(() -> validator.validate(promo, LocalDate.now(), new BigDecimal("100.00")));
    }

    @ParameterizedTest(name = "BL-70: порушення довжини коду за SRS (менше 6 або більше 10 символів: {0}) відхиляється")
    @ValueSource(strings = {"SAVE5", "SUPERSALE2026", "ABC12"})
    void invalidLengthCodeThrows(String codeStr) {
        PromoCode promo = new PromoCode(codeStr, new BigDecimal("10.00"));
        assertThrows(IllegalArgumentException.class, 
            () -> validator.validate(promo, LocalDate.now(), new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("BL-70: перевірка застосування знижки через метод apply(...)")
    void applyDiscountValidCode() {
        PromoCode promo = new PromoCode("SALE2026", new BigDecimal("15.00"));
        BigDecimal initialPrice = new BigDecimal("100.00");
        
        BigDecimal result = validator.apply(promo, initialPrice);
        assertNotNull(result, "Результат застосування промокоду не повинен бути null");
    }
}
