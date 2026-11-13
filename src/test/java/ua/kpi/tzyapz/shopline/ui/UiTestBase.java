package ua.kpi.tzyapz.shopline.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

/**
 * Спільний життєвий цикл UI-тестів «ШопЛайн» на резервному demo Sauce Demo: новий браузер на кожен тест,
 * явні очікування, driver.quit() після кожного тесту.
 * Запуск: mvn test -DexcludedGroups=none; з видимим браузером (демо): додати -Dheadless=false
 */
@Tag("ui")
abstract class UiTestBase {

    protected static final String BASE_URL = "https://www.saucedemo.com/";
    protected static final String USER = "standard_user";
    protected static final String PASSWORD = "secret_sauce";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "true"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1280,900");
        // Chrome показує спливне вікно «пароль знайдено у витоку даних» для secret_sauce, і воно перехоплює кліки
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        options.addArguments("--disable-features=PasswordLeakDetection");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void login(String user, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(user);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
    }

    protected void loginAsStandardUser() {
        login(USER, PASSWORD);
        wait.until(ExpectedConditions.urlContains("/inventory.html"));
    }

    protected void addToCart(String productSlug) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test='add-to-cart-" + productSlug + "']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='remove-" + productSlug + "']")));
    }

    protected void openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(By.className("shopping_cart_link"))).click();
        wait.until(ExpectedConditions.urlContains("/cart.html"));
    }

    /** "$29.99" або "Item total: $29.99" → 29.99 */
    protected static BigDecimal money(String text) {
        return new BigDecimal(text.substring(text.indexOf('$') + 1).trim());
    }
}
