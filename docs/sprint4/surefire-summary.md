# Протокол запуску автотестів (Surefire) — Спринт 4

**Дата:** 12.11.2026, 08:51–08:53
**Стан репозиторію:** гілка `main`, коміт `9afaec7`
**Середовище:** macOS (Apple Silicon), JDK 17, Maven 3.9, Chrome 157
**Виконала:** Белінська Єлизавета (КТ)

## Запуск 1 — юніт-тести

**Команда:** `mvn clean test`

**Підсумок:** `Tests run: 91, Failures: 11, Errors: 0, Skipped: 0` → BUILD FAILURE

Усі 11 падінь — очікувані: кожне відповідає звіту про дефект модуля v1 (GitHub Issues, мітка `module`). Очікувані значення в тестах взято з тексту BL-вимог специфікації, тому «червоний» тест = знайдений дефект, а не помилка тесту.

| Тест (failed) | Розбіжність | Дефект |
|---|---|---|
| `DiscountCalculatorTest.volumeDiscountPercent_boundaries[4]` | subtotal 5000.00: expected 10 %, was 5 % | BUG-015 |
| `DiscountCalculatorTest.volumeDiscount_roundsHalfUp` | 150.005: expected 150.01 (HALF_UP), was 150.00 | BUG-016 |
| `CartTest.addItemWithQtyOutOfRangeIsRejected[4]` | qty=100 прийнято, очікувався виняток (межа 99) | BUG-017 |
| `CartTest.changeQtyOutOfRangeIsRejected[3]` | qty=100 прийнято, очікувався виняток | BUG-017 |
| `CartTest.reAddingSameSkuAboveMaxQtyIsRejected` | сумарна qty > 99 прийнята без винятку | BUG-017 |
| `CartNormalCasesTest.reAddingSameSkuIncreasesExistingLine` | 2+3: expected qty 5, was 3 (рядок замінено) | BUG-018 |
| `CartNormalCasesTest.reAddingUpToMaxQtyIsAccepted` | 2+97: expected qty 99, was 97 | BUG-018 |
| `PromoCodeValidatorRulesTest.expiryDateIsInclusive[2]` | у день закінчення код відхилено (має бути придатний включно) | BUG-019 |
| `PromoCodeValidatorRulesTest.singleUseCodeMustNotBeUsed[2]` | використаний одноразовий код визнано придатним | BUG-020 |
| `PromoCodeValidatorRulesTest.applyRejectsUsedSingleUseCode` | apply не кинув виняток для використаного коду | BUG-020 |
| `PromoCodeValidatorReasonsTest.usedCodeReasonHasPriorityOverInvalidValue` | причина «використано» ігнорується, повернено причину про значення | BUG-020 |

Решта 80 тестів — зелені, зокрема `PromoCodeValidatorTest` (13/13) і `ExampleTest` (2/2).

## Запуск 2 — повний набір з UI

**Команда:** `mvn test -DexcludedGroups=none`

**Підсумок:** `Tests run: 99, Failures: 11, Errors: 1, Skipped: 0` → BUILD FAILURE

- Юніт-частина: ті самі 11 падінь, що в запуску 1 (дефекти BUG-015…020).
- UI (Sauce Demo, headless Chrome): `LoginTest` 3/3 ✅, `CatalogTest` 3/3 ✅, `CheckoutTest` 1/2 — TC-018 ✅, **TC-019 — Error** (`NoSuchElementException: #checkout`).

Помилка TC-019 — **не дефект продукту, а нестабільність тесту**: елемент `#checkout` шукається прямим `findElement` без явного очікування (CheckoutTest.java:55). Передано Інженеру з тестування на стабілізацію (задача S4-19): замінити на `WebDriverWait` + `ExpectedConditions`. `WARNING: Unable to find CDP implementation matching 157` — некритичне попередження про версію DevTools-протоколу, на результат тестів не впливає.

## Висновок

Проєкт збирається і запускається однією командою. Юніт-набір покриває всі 3 класи модуля (норматив: 38 тестів, ≥3 на кожного учасника). Усі червоні тести простежуються до звітів про дефекти BUG-015…BUG-020; після стабілізації TC-019 UI-набір очікується 8/8.