package ua.kpi.tzyapz.shopline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Перевірка і застосування промокодів «ШопЛайн» (правила BL-70, BL-80).
 */
public class PromoCodeValidator {

    private static final Pattern FORMAT = Pattern.compile("[A-Z0-9]{6,10}");
    private static final BigDecimal MIN_PERCENT = BigDecimal.ONE;
    private static final BigDecimal MAX_PERCENT = BigDecimal.valueOf(50);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /** Результат перевірки: ознака придатності та причина відмови (порожня, якщо код придатний). */
    public record ValidationResult(boolean valid, String reason) {
        static ValidationResult ok() {
            return new ValidationResult(true, "");
        }

        static ValidationResult fail(String reason) {
            return new ValidationResult(false, reason);
        }
    }

    /**
     * Перевіряє, чи можна застосувати промокод до замовлення.
     * Умови: формат 6–10 символів A–Z0–9; дата закінчення не раніша за сьогодні (включно);
     * одноразовий код ще не використаний; для типу PERCENT значення 1..50;
     * для типу FIXED сума більша за 0 і не перевищує проміжний підсумок.
     *
     * @param promo    промокод
     * @param today    поточна дата
     * @param subtotal проміжний підсумок кошика
     * @return результат із причиною відмови
     */
    public ValidationResult validate(PromoCode promo, LocalDate today, BigDecimal subtotal) {
        Objects.requireNonNull(promo, "promo");
        Objects.requireNonNull(today, "today");
        Objects.requireNonNull(subtotal, "subtotal");
        if (promo.code() == null || !FORMAT.matcher(promo.code()).matches()) {
            return ValidationResult.fail("Невірний формат промокоду");
        }
        if (promo.expiresOn() == null || !today.isBefore(promo.expiresOn())) {
            return ValidationResult.fail("Термін дії промокоду минув");
        }
        if (promo.value() == null) {
            return ValidationResult.fail("Не вказано значення знижки");
        }
        switch (promo.type()) {
            case PERCENT -> {
                if (promo.value().compareTo(MIN_PERCENT) < 0 || promo.value().compareTo(MAX_PERCENT) > 0) {
                    return ValidationResult.fail("Відсоток знижки має бути в межах 1..50");
                }
            }
            case FIXED -> {
                if (promo.value().signum() <= 0 || promo.value().compareTo(subtotal) > 0) {
                    return ValidationResult.fail("Фіксована знижка має бути більшою за 0 і не перевищувати підсумок");
                }
            }
        }
        return ValidationResult.ok();
    }

    /**
     * Обчислює суму знижки за промокодом.
     *
     * @param promo    промокод, що пройшов перевірку
     * @param today    поточна дата
     * @param subtotal проміжний підсумок кошика
     * @return для PERCENT — subtotal × відсоток / 100 (HALF_UP до копійок); для FIXED — сума коду
     * @throws IllegalArgumentException якщо код не пройшов перевірку (у повідомленні — причина)
     */
    public BigDecimal apply(PromoCode promo, LocalDate today, BigDecimal subtotal) {
        ValidationResult result = validate(promo, today, subtotal);
        if (!result.valid()) {
            throw new IllegalArgumentException(result.reason());
        }
        return switch (promo.type()) {
            case PERCENT -> subtotal.multiply(promo.value()).divide(HUNDRED, 2, RoundingMode.HALF_UP);
            case FIXED -> promo.value().setScale(2, RoundingMode.HALF_UP);
        };
    }
}
