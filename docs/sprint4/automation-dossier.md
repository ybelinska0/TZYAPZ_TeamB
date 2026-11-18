# Розділ «Автоматизація» — Досьє якості v4

**Продукт:** П1 «ШопЛайн» (`shopline`)  
**Спринт:** 4 «Автоматизація: JUnit 5 і Selenium WebDriver»  
**Репозиторій:** https://github.com/ybelinska0/TZYAPZ_TeamB  
**Трекер:** https://shopline1teamb.atlassian.net/jira/software/projects/TTS  
**Склав:** Белінська Єлизавета (КТ)  
**Дата:** 17.11.2026

---

## 1. Нормативні показники (план / факт)

| Показник | Норматив | Факт |
|---|---:|---:|
| Унікальні юніт-тест-методи JUnit 5 | ≥ 20 | **41** |
| Юніт-тест-методів на учасника | ≥ 3 | **4–13 на учасника** |
| Виконання юніт-тестів у Run 4 | — | **96** |
| UI-сценаріїв WebDriver | ≥ 8 | **8** |
| Виконання всіх тестів у Run 5 | — | **104** |
| Дефектів модуля з відповідними тестами | ≥ 5 | **6** (BUG-015…BUG-020) |
| Матриця покриття | є | `coverage-matrix.md` |
| Документ UI-сценаріїв | є | `ui-scenarios.md` |
| Протокол Surefire | є | `surefire-summary.md` — 5 запусків, з них 3 історичні та 2 фінальні |
| Протокол відтворення дефектів | є | `reproduction-protocol.md` |
| Журнал code review | є | `code-review.md` |

**Примітка щодо кількості тестів:** 41 — це кількість унікальних юніт-тест-методів у шести основних тест-класах. Surefire показує 96 виконань у Run 4 через параметризовані тести та 2 додаткові виконання `ExampleTest` зі стартового проєкту. Під час повного запуску Run 5 додаються 8 UI-тестів, тому загальна кількість становить 104.

---

## 2. Структура автоматизованих тестів

```text
TZYAPZ_TeamB/
├── README.md
├── pom.xml
├── docs/
│   └── sprint4/
│       ├── coverage-matrix.md
│       ├── ui-scenarios.md
│       ├── surefire-summary.md
│       ├── reproduction-protocol.md
│       ├── code-review.md
│       ├── task-board.md
│       └── automation-dossier.md
├── selenium-ide/
│   └── shopline.side
└── src/
    └── test/
        └── java/ua/kpi/tzyapz/shopline/
            ├── CartTest.java
            ├── CartNormalCasesTest.java
            ├── DiscountCalculatorTest.java
            ├── PromoCodeValidatorTest.java
            ├── PromoCodeValidatorRulesTest.java
            ├── PromoCodeValidatorReasonsTest.java
            └── ui/
                ├── LoginTest.java
                ├── CatalogTest.java
                ├── CheckoutTest.java
                └── UiTestBase.java
```

**Основні інструменти:** JDK 17, Maven, JUnit 5, Selenium WebDriver, Maven Surefire, Chrome та Selenium Manager.

---

## 3. Кількість юніт-тест-методів за класами

У шести основних тест-класах реалізовано 41 унікальний юніт-тест-метод.

| Тест-клас | Кількість методів | Що перевіряється |
|---|---:|---|
| `CartTest` | 7 | Заборонені значення, межі кількості, винятки кошика |
| `CartNormalCasesTest` | 8 | Допустимі значення, нормальні сценарії кошика, повторне додавання товарів |
| `DiscountCalculatorTest` | 7 | Тарифи знижок, округлення, вартість доставки |
| `PromoCodeValidatorTest` | 4 | Формат промокоду, довжина та допустимі символи |
| `PromoCodeValidatorRulesTest` | 10 | Термін дії, одноразовість, межі значення, застосування знижки |
| `PromoCodeValidatorReasonsTest` | 5 | Причини відмови, їх пріоритет, застосування промокоду |
| **Разом** | **41** | |

### Покриття бізнес-логіки

| BL-вимога | Тест-класи | Основні перевірки |
|---|---|---|
| BL-10 | `CartTest`, `CartNormalCasesTest` | Межі кількості товару, допустимі та недопустимі значення |
| BL-20 | `CartTest`, `CartNormalCasesTest` | Повторне додавання товару, збільшення кількості в наявному рядку |
| BL-40 | `CartTest`, `CartNormalCasesTest` | Зміна кількості, видалення товару, підсумок кошика |
| BL-50 | `DiscountCalculatorTest` | Межі тарифів знижки та округлення `HALF_UP` |
| BL-60 | `DiscountCalculatorTest` | Розрахунок вартості доставки |
| BL-70 | `PromoCodeValidatorTest`, `PromoCodeValidatorRulesTest`, `PromoCodeValidatorReasonsTest` | Формат, термін дії, одноразовість, допустимі значення |
| BL-80 | `PromoCodeValidatorRulesTest`, `PromoCodeValidatorReasonsTest` | Застосування промокоду та перевірка його придатності |

Параметризовані тести перевіряють декілька наборів вхідних даних одним методом. Кожен набір даних виконується Surefire окремо, тому кількість виконань більша за кількість унікальних методів.

---

## 4. Перелік UI-сценаріїв (Selenium WebDriver)

UI-набір складається з 8 тест-методів у трьох класах: `LoginTest`, `CatalogTest` і `CheckoutTest`.

| Тест-метод | TC-ID | Призначення | Результат фінального запуску |
|---|---|---|---|
| `LoginTest.validLoginOpensInventory()` | TC-004 | Успішний вхід і відкриття каталогу | PASSED |
| `LoginTest.invalidPasswordShowsError()` | TC-005 | Відхилення входу з неправильним паролем | PASSED |
| `LoginTest.inventoryIsNotAccessibleAfterLogout()` | TC-024 | Перевірка недоступності каталогу після виходу | PASSED |
| `CatalogTest.catalogShowsCompleteProductCards()` | TC-007 | Перевірка карток товарів у каталозі | PASSED |
| `CatalogTest.sortByPriceLowToHighOrdersAscending()` | TC-012 | Сортування товарів за зростанням ціни | PASSED |
| `CatalogTest.productPageShowsDetails()` | TC-013 | Перевірка сторінки товару | PASSED |
| `CheckoutTest.cartShowsItemAndRemovingItEmptiesCart()` | TC-018 | Перевірка кошика та видалення товару | PASSED |
| `CheckoutTest.checkoutWithTwoItemsCompletesOrder()` | TC-019 | Оформлення замовлення з двома товарами | PASSED |

### Результати UI-набору

| Клас | Успішно | Всього |
|---|---:|---:|
| `LoginTest` | 3 | 3 |
| `CatalogTest` | 3 | 3 |
| `CheckoutTest` | 2 | 2 |
| **Разом** | **8** | **8** |

У Run 5 всі 8 UI-тестів пройшли успішно двічі поспіль.

**Стабілізація TC-019:** у PR #48 виправлено очікування елементів Selenium із використанням `WebDriverWait` та `ExpectedConditions`. Тестові дані для оформлення замовлення виправлено в PR #53: використовуються `Test`, `User`, `12345`.

**Примітка щодо тестового середовища:** UI-сценарії виконуються на резервному demo-застосунку Sauce Demo замість основного Toolshop. **Дозвіл викладача на цю заміну отримано** (підтверджено 18.11.2026) — див. README і `ui-scenarios.md`. Відмінності між початковими кейсами та резервним demo описані в `ui-scenarios.md`.

---

## 5. Підсумок запусків Surefire

Детальний протокол: `surefire-summary.md`.

### Фінальні запуски

| Запуск | Дата | Команда | Tests run | Failures | Errors | Skipped |
|---|---|---|---:|---:|---:|---:|
| Run 4 — Белінська | 17.11.2026, 22:03:53 (UTC+02:00) | `mvn clean test` | 96 | 11 | 0 | 0 |
| Run 5 — Бобров, запуск 1 | 17.11.2026, 21:45 | `mvn clean test -DexcludedGroups=none` | 104 | 11 | 0 | 0 |
| Run 5 — Бобров, запуск 2 | 17.11.2026, 21:45 | `mvn clean test -DexcludedGroups=none` | 104 | 11 | 0 | 0 |

**Run 4:** виконано чистий запуск юніт-тестів. Отримано 96 виконань, 11 падінь і 0 помилок виконання.

**Run 5:** виконано повний набір, включно з UI-тестами. Обидва повторні запуски дали однаковий результат: 104 виконання, 11 падінь і 0 помилок виконання. Усі 8 UI-тестів пройшли успішно.

### Історичні запуски

| Запуск | Дата | Команда | Коміт | Tests run | Failures | Errors | Skipped |
|---|---|---|---|---:|---:|---:|---:|
| Run 1 | 12.11.2026 | `mvn clean test` | `9afaec7` | 91 | 11 | 0 | 0 |
| Run 2 | 12.11.2026 | `mvn test -DexcludedGroups=none` | `9afaec7` | 99 | 11 | 1 | 0 |
| Run 3 | 13.11.2026 | `mvn test -DexcludedGroups=none` | `4404ea9` | 99 | 11 | 0 | 0 |

У Run 2 виникла помилка UI-тесту TC-019 через нестабільне очікування елемента `#checkout`. Її було усунуто в PR #48. У PR #53 виправлено тестові дані оформлення замовлення. Після цього Run 3 і фінальний Run 5 підтвердили успішне виконання UI-набору.

---

## 6. Дефекти модуля v1

У процесі тестування виявлено шість дефектів бізнес-логіки. Кожен дефект має відповідний GitHub Issue та пов'язаний із тестами, які відтворюють проблему.

| ID | GitHub Issue | BL-вимога | Тест, що виявляє | Severity |
|---|---|---|---|---|
| BUG-015 | #27 | BL-50 | `DiscountCalculatorTest.volumeDiscountPercent_boundaries` | Major |
| BUG-016 | #28 | BL-50 | `DiscountCalculatorTest.volumeDiscount_roundsHalfUp` | Minor |
| BUG-017 | #29 | BL-10, BL-40 | `CartTest.addItemWithQtyOutOfRangeIsRejected`, `CartTest.changeQtyOutOfRangeIsRejected` | Normal |
| BUG-018 | #30 | BL-20 | `CartTest.reAddingSameSkuAboveMaxQtyIsRejected`, `CartNormalCasesTest.reAddingSameSkuIncreasesExistingLine`, `CartNormalCasesTest.reAddingUpToMaxQtyIsAccepted` | Major |
| BUG-019 | #50 | BL-70 | `PromoCodeValidatorRulesTest.expiryDateIsInclusive` | Normal |
| BUG-020 | #38 | BL-70, BL-80 | `PromoCodeValidatorRulesTest.singleUseCodeMustNotBeUsed`, `PromoCodeValidatorRulesTest.applyRejectsUsedSingleUseCode`, `PromoCodeValidatorReasonsTest.usedCodeReasonHasPriorityOverInvalidValue` | Major |

### Прив'язка дефектів до класів

- **Cart:** BUG-017 — прийняття кількості 100 замість обмеження 1–99; BUG-018 — неправильна поведінка під час повторного додавання товару з тим самим SKU.
- **DiscountCalculator:** BUG-015 — неправильний тариф знижки на межі 5000 грн; BUG-016 — неправильне округлення суми знижки.
- **PromoCodeValidator:** BUG-019 — відхилення промокоду в день завершення його дії; BUG-020 — прийняття вже використаного одноразового промокоду та неправильна обробка відповідної причини відмови.

**Примітка щодо BUG-020:** перевірка пріоритету причини «Код уже використано» над недопустимим значенням знижки спирається на запропоноване уточнення BL-70/BL-80, яке ще очікує підтвердження замовника. Основні перевірки одноразовості промокоду та відхилення його застосування залишаються актуальними незалежно від цього уточнення.

---

## 7. Внесок учасників

Розподіл тест-методів за авторами відповідно до `README.md` та історії роботи над тестами.

| Учасник | Роль | Тест-класи / артефакти | Кількість юніт-методів |
|---|---|---|---:|
| Белінська Єлизавета | КТ | `DiscountCalculatorTest` — тарифи, нормальні випадки, округлення та доставка | 5 |
| Кочаров Давид | АО | `CartTest` — заборонені входи та винятки кошика | 7 |
| Бойко Євгеній | СМ | Частина `DiscountCalculatorTest`, `PromoCodeValidatorRulesTest`, code review | 12 |
| Матвієнко Владислав | ТА | `PromoCodeValidatorTest` — формат промокоду | 4 |
| Бобров Павло | ІТ | `CartNormalCasesTest`, `PromoCodeValidatorReasonsTest`, UI-тести та `UiTestBase` | 13 |
| **Разом** | | | **41** |

Окремо реалізовано 8 UI-тестів WebDriver.

---

## 8. Висновок про стан автоматизації

У межах спринта 4 підготовлено автоматизований набір для перевірки бізнес-логіки модуля `shopline` та основних UI-сценаріїв.

**Юніт-тестування:**

- 41 унікальний тест-метод у шести основних тест-класах;
- 96 виконань у фінальному Run 4;
- 11 невдалих виконань, усі пов'язані з раніше зареєстрованими дефектами;
- 0 помилок виконання.

**UI-тестування:**

- 8 сценаріїв WebDriver у трьох тест-класах;
- 8 із 8 тестів успішно виконано в Run 5;
- результат повторився у двох послідовних повних запусках;
- TC-019 стабілізовано в PR #48, тестові дані виправлено в PR #53.

**Виявлені дефекти:**

- `Cart` — BUG-017 і BUG-018;
- `DiscountCalculator` — BUG-015 і BUG-016;
- `PromoCodeValidator` — BUG-019 і BUG-020.

**Стан модуля v1:** модуль v1 не готовий до випуску, оскільки містить 6 зареєстрованих дефектів у всіх трьох класах бізнес-логіки (`Cart`, `DiscountCalculator`, `PromoCodeValidator`). Із них 3 мають рівень серйозності **Major**: BUG-015, BUG-018 і BUG-020. До випуску необхідно усунути дефекти й повторно виконати регресійні тести.

Усі 11 невдалих тестів мають відповідні зареєстровані дефекти. Нових падінь та помилок виконання під час фінальних запусків не виявлено.

Отже, набір автоматизованих тестів підготовлений для регресійної перевірки виправлень модуля v1 у наступному спринті. Після усунення зареєстрованих дефектів очікується успішне проходження всіх юніт- та UI-тестів.
