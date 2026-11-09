package ua.kpi.tzyapz.shopline;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Знижки за обсяг і вартість доставки «ШопЛайн» (правила BL-50, BL-60).
 * Усі суми — у гривнях, результат округлюється до копійок (HALF_UP).
 */
public class DiscountCalculator {

    private static final BigDecimal TIER_5 = new BigDecimal("2000");
    private static final BigDecimal TIER_10 = new BigDecimal("5000");
    private static final BigDecimal TIER_15 = new BigDecimal("10000");
    private static final BigDecimal FREE_SHIPPING_FROM = new BigDecimal("1500");
    private static final BigDecimal SHIPPING_FEE = new BigDecimal("120.00");
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /**
     * Відсоток знижки за обсяг: від 2000 грн — 5 %, від 5000 грн — 10 %, від 10000 грн — 15 %.
     * Застосовується лише один, найвищий досягнутий рівень.
     *
     * @param subtotal проміжний підсумок кошика, не від'ємний
     * @return 0, 5, 10 або 15
     */
    public int volumeDiscountPercent(BigDecimal subtotal) {
        requireNonNegative(subtotal);
        if (subtotal.compareTo(TIER_15) >= 0) {
            return 15;
        }
        if (subtotal.compareTo(TIER_10) > 0) {
            return 10;
        }
        if (subtotal.compareTo(TIER_5) >= 0) {
            return 5;
        }
        return 0;
    }

    /**
     * Сума знижки за обсяг у гривнях.
     *
     * @param subtotal проміжний підсумок кошика, не від'ємний
     * @return subtotal × відсоток / 100, округлено до копійок
     */
    public BigDecimal volumeDiscount(BigDecimal subtotal) {
        int percent = volumeDiscountPercent(subtotal);
        return subtotal.multiply(BigDecimal.valueOf(percent))
                .divide(HUNDRED, 2, RoundingMode.DOWN);
    }

    /**
     * Вартість доставки: 0 грн, якщо сума після знижки не менша за 1500 грн, інакше 120 грн.
     *
     * @param subtotalAfterDiscount сума замовлення після знижок, не від'ємна
     * @return 0.00 або 120.00
     */
    public BigDecimal shippingFee(BigDecimal subtotalAfterDiscount) {
        requireNonNegative(subtotalAfterDiscount);
        if (subtotalAfterDiscount.compareTo(FREE_SHIPPING_FROM) >= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return SHIPPING_FEE;
    }

    private static void requireNonNegative(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Сума не може бути від'ємною або порожньою");
        }
    }
}
