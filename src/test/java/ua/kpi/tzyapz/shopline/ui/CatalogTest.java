package ua.kpi.tzyapz.shopline.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тести каталогу і сторінки товару — сценарії TC-007, TC-012, TC-013 спринта 3,
 * перенесені на резервний demo Sauce Demo.
 */
@Tag("ui")
@DisplayName("ШопЛайн (Sauce Demo): каталог, сортування, сторінка товару")
class CatalogTest extends UiTestBase {

    private static final By ITEM = By.className("inventory_item");
    private static final By PRICE = By.className("inventory_item_price");

    @Test
    @DisplayName("TC-007: каталог показує 6 карток, у кожній є назва, ціна і кнопка «Add to cart»")
    void catalogShowsCompleteProductCards() {
        loginAsStandardUser();

        List<WebElement> cards = wait.until(ExpectedConditions.numberOfElementsToBe(ITEM, 6));

        for (WebElement card : cards) {
            assertAll("Картка товару",
                    () -> assertFalse(card.findElement(By.className("inventory_item_name")).getText().isBlank(), "назва"),
                    () -> assertTrue(card.findElement(PRICE).getText().startsWith("$"), "ціна"),
                    () -> assertEquals("Add to cart", card.findElement(By.tagName("button")).getText(), "кнопка"));
        }
    }

    @Test
    @DisplayName("TC-012: сортування «Price (low to high)» впорядковує ціни за зростанням")
    void sortByPriceLowToHighOrdersAscending() {
        loginAsStandardUser();

        new Select(wait.until(ExpectedConditions.elementToBeClickable(By.className("product_sort_container"))))
                .selectByVisibleText("Price (low to high)");

        List<BigDecimal> prices = wait.until(d -> {
            List<BigDecimal> found = d.findElements(PRICE).stream().map(e -> money(e.getText())).toList();
            return found.size() == 6 && found.get(0).compareTo(found.get(5)) < 0 ? found : null;
        });
        List<BigDecimal> sorted = prices.stream().sorted().toList();
        assertIterableEquals(sorted, prices);
    }

    @Test
    @DisplayName("TC-013: сторінка товару містить назву, опис, ціну, кнопку «Add to cart» і повернення до каталогу")
    void productPageShowsDetails() {
        loginAsStandardUser();

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sauce Labs Bolt T-Shirt"))).click();

        wait.until(ExpectedConditions.urlContains("/inventory-item.html"));
        String name = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("inventory_details_name"))).getText();
        assertAll("Сторінка товару",
                () -> assertEquals("Sauce Labs Bolt T-Shirt", name, "назва"),
                () -> assertFalse(driver.findElement(By.className("inventory_details_desc")).getText().isBlank(), "опис"),
                () -> assertEquals("$15.99", driver.findElement(By.className("inventory_details_price")).getText(), "ціна"),
                () -> assertTrue(driver.findElement(By.id("add-to-cart")).isDisplayed(), "кнопка «Add to cart»"),
                () -> assertTrue(driver.findElement(By.id("back-to-products")).isDisplayed(), "повернення до каталогу"));
    }
}
