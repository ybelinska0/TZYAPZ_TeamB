package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


@Tag("unit")
@DisplayName("PromoCodeValidator - валідація формату коду")
class PromoCodeValidatorTest {

    private PromoCodeValidator validator;

    @BeforeEach
    void setup() {
        validator = new PromoCodeValidator();
    }

    @Test
    @DisplayName("BL-50: null або порожній промокод генерує IllegalArgumentException")
    void nullOrBlankCodeThrows() {
        
        String nullCode = null;
        String blankCode = "   ";

     
        assertAll("Перевірка порожніх входів",
            () -> assertThrows(IllegalArgumentException.class, 
                    () -> validator.isValid(nullCode), "Має кидати виняток для null"),
            () -> assertThrows(IllegalArgumentException.class, 
                    () -> validator.isValid(blankCode), "Має кидати виняток для пробілів")
        );
    }

    @ParameterizedTest(name = "BL-50: коректний формат {0} проходить валідацію")
    @ValueSource(strings = {"SAVE5", "PROMOCODE1", "WINTER2026"})
    void validFormatReturnsTrue(String validCode) {
       
        boolean result = validator.isValid(validCode);

  
        assertTrue(result, "Промокод із правильним форматом має бути валідним");
    }

    @ParameterizedTest(name = "BL-50: порушення формату (довжина або символи) {0} відхиляється")
    @ValueSource(strings = {
        "SAV",          
        "SUPERSALE2026",
        "save20",      
        "SAVE!20",      
        "ПРОМО"        
    })
    void invalidFormatThrows(String invalidCode) {
      
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.isValid(invalidCode));
        
        assertNotNull(ex.getMessage());
    }
}
