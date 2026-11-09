package ua.kpi.tzyapz.shopline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Кошик покупця «ШопЛайн» (правила BL-10, BL-20, BL-40).
 * Позиції зберігаються у порядку додавання; ключ позиції — артикул (SKU).
 * Усі суми — у гривнях, округлення до копійок за правилом HALF_UP.
 */
public class Cart {

    /** Мінімальна кількість одиниць в одному рядку кошика. */
    public static final int MIN_QTY = 1;
    /** Максимальна кількість одиниць в одному рядку кошика. */
    public static final int MAX_QTY = 100;

    /** Рядок кошика: артикул, назва, ціна за одиницю (грн), кількість. */
    public record CartItem(String sku, String name, BigDecimal unitPrice, int qty) {
        /** Вартість рядка = ціна × кількість, округлена до копійок (HALF_UP). */
        public BigDecimal lineTotal() {
            return unitPrice.multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP);
        }
    }

    private final Map<String, CartItem> items = new LinkedHashMap<>();

    /**
     * Додає товар до кошика.
     *
     * @param sku       артикул, непорожній рядок
     * @param name      назва товару
     * @param unitPrice ціна за одиницю, строго більша за 0
     * @param qty       кількість від 1 до 99 включно
     * @throws IllegalArgumentException якщо параметри поза допустимими межами; кошик не змінюється
     */
    public void addItem(String sku, String name, BigDecimal unitPrice, int qty) {
        requireSku(sku);
        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Ціна має бути більшою за 0");
        }
        requireQty(qty);
        BigDecimal price = unitPrice.setScale(2, RoundingMode.HALF_UP);
        items.put(sku, new CartItem(sku, name, price, qty));
    }

    /**
     * Видаляє рядок із кошика.
     *
     * @param sku артикул наявного рядка
     * @throws NoSuchElementException якщо рядка з таким артикулом немає
     */
    public void removeItem(String sku) {
        if (items.remove(sku) == null) {
            throw new NoSuchElementException("У кошику немає товару " + sku);
        }
    }

    /**
     * Встановлює нову кількість для наявного рядка.
     *
     * @param sku артикул наявного рядка
     * @param qty нова кількість від 1 до 99 включно
     * @throws NoSuchElementException   якщо рядка з таким артикулом немає
     * @throws IllegalArgumentException якщо кількість поза допустимими межами
     */
    public void changeQty(String sku, int qty) {
        CartItem item = find(sku);
        requireQty(qty);
        items.put(sku, new CartItem(sku, item.name(), item.unitPrice(), qty));
    }

    /** Проміжний підсумок: сума вартостей усіх рядків, округлена до копійок (HALF_UP). */
    public BigDecimal subtotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            sum = sum.add(item.lineTotal());
        }
        return sum.setScale(2, RoundingMode.HALF_UP);
    }

    /** Загальна кількість одиниць товару в кошику (сума кількостей усіх рядків). */
    public int itemCount() {
        return items.values().stream().mapToInt(CartItem::qty).sum();
    }

    /** Кількість одиниць у рядку з указаним артикулом. */
    public int qtyOf(String sku) {
        return find(sku).qty();
    }

    /** Рядки кошика у порядку додавання (лише для читання). */
    public Collection<CartItem> items() {
        return Collections.unmodifiableCollection(items.values());
    }

    private CartItem find(String sku) {
        CartItem item = items.get(sku);
        if (item == null) {
            throw new NoSuchElementException("У кошику немає товару " + sku);
        }
        return item;
    }

    private static void requireSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("Артикул не може бути порожнім");
        }
    }

    private static void requireQty(int qty) {
        if (qty < MIN_QTY || qty > MAX_QTY) {
            throw new IllegalArgumentException("Кількість поза допустимими межами");
        }
    }
}
