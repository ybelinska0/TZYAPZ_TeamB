package ua.kpi.tzyapz.shopline;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Промокод програми лояльності «ШопЛайн» (дані для {@link PromoCodeValidator}).
 *
 * @param code      текст коду (очікується 6–10 символів A–Z, 0–9)
 * @param type      тип знижки: відсоток від проміжного підсумку або фіксована сума в гривнях
 * @param value     значення: для PERCENT — ціле число відсотків, для FIXED — сума у гривнях
 * @param expiresOn остання дата дії коду (включно)
 * @param singleUse чи є код одноразовим
 * @param used      чи вже використано одноразовий код
 */
public record PromoCode(String code, Type type, BigDecimal value,
                        LocalDate expiresOn, boolean singleUse, boolean used) {

    /** Тип промокоду. */
    public enum Type { PERCENT, FIXED }
}
