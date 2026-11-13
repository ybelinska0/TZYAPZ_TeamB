package ua.kpi.tzyapz.shopline.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тести кошика і оформлення замовлення — сценарії TC-018, TC-019 спринта 3,
 * перенесені на резервний demo Sauce Demo.
 */
@Tag("ui")
@DisplayName("ШопЛайн (Sauce Demo): кошик і оформлення замовлення")
class CheckoutTest extends UiTestBase {

    private static final String BACKPACK = "sauce-labs-backpack";
    private static final String BIKE_LIGHT = "sauce-labs-bike-light";

    @Test
    @DisplayName("TC-018: склад кошика (назва, кількість, ціна) і видалення єдиної позиції спорожнює кошик")
    void cartShowsItemAndRemovingItEmptiesCart() {
        loginAsStandardUser();
        addToCart(BACKPACK);
        openCart();

        By item = By.className("cart_item");
        wait.until(ExpectedConditions.numberOfElementsToBe(item, 1));
        assertAll("Рядок кошика",
                () -> assertEquals("Sauce Labs Backpack",
                        driver.findElement(By.className("inventory_item_name")).getText(), "назва"),
                () -> assertEquals("1", driver.findElement(By.className("cart_quantity")).getText(), "кількість"),
                () -> assertEquals("$29.99",
                        driver.findElement(By.className("inventory_item_price")).getText(), "ціна"),
                () -> assertEquals("1", driver.findElement(By.className("shopping_cart_badge")).getText(), "лічильник"));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test='remove-" + BACKPACK + "']"))).click();

        wait.until(ExpectedConditions.numberOfElementsToBe(item, 0));
        assertTrue(driver.findElements(By.className("shopping_cart_badge")).isEmpty(),
                "лічильник кошика зник");
    }

    @Test
    @DisplayName("TC-019: оформлення замовлення з двома товарами — підсумок = сума цін + податок, підтвердження замовлення")
    void checkoutWithTwoItemsCompletesOrder() {
        loginAsStandardUser();
        addToCart(BACKPACK);
        addToCart(BIKE_LIGHT);
        openCart();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name"))).sendKeys("Pavlo");
        driver.findElement(By.id("last-name")).sendKeys("Bobrov");
        driver.findElement(By.id("postal-code")).sendKeys("03056");
        driver.findElement(By.id("continue")).click();

        wait.until(ExpectedConditions.urlContains("/checkout-step-two.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("summary_total_label")));
        BigDecimal itemTotal = money(driver.findElement(By.className("summary_subtotal_label")).getText());
        BigDecimal tax = money(driver.findElement(By.className("summary_tax_label")).getText());
        BigDecimal total = money(driver.findElement(By.className("summary_total_label")).getText());
        assertAll("Підсумок замовлення",
                () -> assertEquals(2, driver.findElements(By.className("cart_item")).size(), "кількість позицій"),
                () -> assertEquals(new BigDecimal("39.98"), itemTotal, "сума цін 29.99 + 9.99"),
                () -> assertEquals(itemTotal.add(tax), total, "Total = Item total + Tax"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("finish"))).click();

        String header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("complete-header")))
                .getText();
        assertAll("Підтвердження",
                () -> assertEquals("Thank you for your order!", header, "заголовок підтвердження"),
                () -> assertTrue(driver.findElements(By.className("shopping_cart_badge")).isEmpty(), "кошик порожній"));
    }
}
