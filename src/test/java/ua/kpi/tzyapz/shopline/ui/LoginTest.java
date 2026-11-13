package ua.kpi.tzyapz.shopline.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тести входу і доступу до кабінету — сценарії TC-004, TC-005, TC-024 спринта 3,
 * перенесені на резервний demo Sauce Demo.
 */
@Tag("ui")
@DisplayName("ШопЛайн (Sauce Demo): вхід і доступ без авторизації")
class LoginTest extends UiTestBase {

    private static final By ERROR = By.cssSelector("[data-test='error']");

    @Test
    @DisplayName("TC-004: вхід із правильними даними відкриває каталог, у шапці з'являються кошик і меню")
    void validLoginOpensInventory() {
        login(USER, PASSWORD);

        wait.until(ExpectedConditions.urlContains("/inventory.html"));
        String title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='title']")))
                .getText();
        assertAll("Сторінка після входу",
                () -> assertEquals("Products", title, "заголовок сторінки"),
                () -> assertTrue(driver.findElement(By.className("shopping_cart_link")).isDisplayed(), "іконка кошика"),
                () -> assertTrue(driver.findElement(By.id("react-burger-menu-btn")).isDisplayed(), "кнопка меню"));
    }

    @Test
    @DisplayName("TC-005: вхід із неправильним паролем відхиляється з повідомленням про помилку")
    void invalidPasswordShowsError() {
        login(USER, "wrong_password");

        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR)).getText();
        assertAll("Помилка входу",
                () -> assertTrue(error.contains("Username and password do not match"), "текст помилки: " + error),
                () -> assertFalse(driver.getCurrentUrl().contains("/inventory.html"), "каталог не відкрито"));
    }

    @Test
    @DisplayName("TC-024: після «Logout» каталог недоступний за прямим посиланням; пароль у полі маскується")
    void inventoryIsNotAccessibleAfterLogout() {
        assertEquals("password", driver.findElement(By.id("password")).getAttribute("type"),
                "поле пароля має тип password");
        loginAsStandardUser();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("react-burger-menu-btn"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
        driver.get(BASE_URL + "inventory.html");

        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR)).getText();
        assertAll("Доступ після виходу",
                () -> assertTrue(error.contains("when you are logged in"), "текст помилки: " + error),
                () -> assertTrue(driver.findElement(By.id("login-button")).isDisplayed(), "показано форму входу"));
    }
}
