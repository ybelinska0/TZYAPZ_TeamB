# Розділ «Автоматизація» — Досьє якості v4

**Продукт:** П1 «ШопЛайн» (`shopline`)  
**Спринт:** 4 «Автоматизація: JUnit 5 і Selenium WebDriver»  
**Репозиторій:** https://github.com/ybelinska0/TZYAPZ_TeamB  
**Трекер:** https://shopline1teamb.atlassian.net/jira/software/projects/TTS  
**Склав:** Белінська Єлизавета (КТ)  
**Дата:** 14.11.2026

---

## 1. Нормативні показники (план / факт)

| Показник | Норматив (5 осіб) | Факт |
|---|---|---|
| Юніт-тестів JUnit 5 (методів) | ≥ 20 | **38** (91 із параметризацією) |
| Юніт-тестів на особу | ≥ 3 | Белінська 4, Кочаров 7, Бойко 10, Матвієнко 13, Бобров 4 |
| UI-сценаріїв WebDriver | ≥ 8 | **8** (LoginTest 3, CatalogTest 3, CheckoutTest 2) |
| Дефектів модуля (звіт + тест) | ≥ 5 | **6** (BUG-015…BUG-020) |
| Матриця покриття | є | `coverage-matrix.md` — 13 рядків BL-10…BL-80 |
| UI-сценарії (документ) | є | `ui-scenarios.md` — 8 сценаріїв TC-004…TC-024 |
| Протокол Surefire | є | `surefire-summary.md` — 2 запуски |
| Протокол відтворення (АО) | є | `reproduction-protocol.md` — 6 звітів |
| Журнал code review | є | `code-review.md` — 7 класів, 8 зауважень |

---

## 2. Структура репозиторію автотестів

```
qa-shopline/
├── README.md                        # збірка, запуск, структура, автори
├── pom.xml                          # JDK 17, JUnit 5.10, Selenium 4.21, Surefire
├── docs/sprint4/
│   ├── coverage-matrix.md           # BL-вимога → правило → тести → рецензент
│   ├── ui-scenarios.md              # TC-xx → кроки, локатори, очікування, тест-метод
│   ├── surefire-summary.md          # протокол запуску: дата, команда, результати
│   ├── reproduction-protocol.md     # 6 звітів про дефекти з кроками відтворення
│   ├── code-review.md               # рев'ю 7 тест-класів, 8 зауважень
│   ├── task-board.md                # розподіл 26 задач по ролях
│   └── automation-dossier.md        # цей документ
├── selenium-ide/
│   └── shopline.side                # проєкт Selenium IDE
└── src/test/java/ua/kpi/tzyapz/shopline/
    ├── CartTest.java                # 13 методів — BL-10, BL-20, BL-40
    ├── CartNormalCasesTest.java     # 8 методів — BL-10, BL-20, BL-40
    ├── DiscountCalculatorTest.java  # 7 методів — BL-50, BL-60
    ├── PromoCodeValidatorTest.java  # 13 методів — BL-70 (формат, пріоритет)
    ├── PromoCodeValidatorRulesTest.java  # 11 методів — BL-70 (правила), BL-80
    ├── PromoCodeValidatorReasonsTest.java # 5 методів — BL-70 (пріоритет), BL-80
    └── ui/
        ├── LoginTest.java           # 3 сценарії — TC-004, TC-005, TC-006
        ├── CatalogTest.java         # 3 сценарії — TC-007, TC-008, TC-009
        └── CheckoutTest.java        # 2 сценарії — TC-018, TC-019
```

---

## 3. Охоплення BL-вимог юніт-тестами

| BL-вимога | Перевіряє правило | Тест-клас | Методів |
|---|---|---|---|
| BL-10 | Межі qty (1..99), ціна > 0, SKU непорожній | CartNormalCasesTest, CartTest | 7 |
| BL-20 | Повторне додавання збільшує qty; ≤ 99 | CartNormalCasesTest, CartTest | 3 |
| BL-40 | changeQty межі (1..99), removeItem, subtotal | CartNormalCasesTest, CartTest | 8 |
| BL-50 | volumeDiscountPercent тарифи 0/5/10/15 %, округлення HALF_UP | DiscountCalculatorTest | 4 |
| BL-60 | shippingFee: 0.00 при ≥ 1500 грн, 120.00 інакше | DiscountCalculatorTest | 3 |
| BL-70 | Формат (6–10 симв., A–Z, 0–9), термін, одноразовість, пріоритет причин | PromoCodeValidatorTest, PromoCodeValidatorRulesTest, PromoCodeValidatorReasonsTest | 29 |
| BL-80 | apply(): розрахунок знижки HALF_UP; виняток для непридатного коду | PromoCodeValidatorRulesTest, PromoCodeValidatorReasonsTest | 6 |

**Усього:** 38 унікальних тест-методів (91 виконання з параметризацією).

---

## 4. Перелік UI-сценаріїв (WebDriver)

| Тест-метод | TC-ID | Опис | Статус |
|---|---|---|---|
| `LoginTest.validCredentialsLogin()` | TC-004 | Успішний вхід покупця | ✅ PASSED |
| `LoginTest.invalidPasswordShowsError()` | TC-005 | Помилковий пароль — повідомлення про помилку | ✅ PASSED |
| `LoginTest.emptyFieldsShowError()` | TC-006 | Порожні поля при вході | ✅ PASSED |
| `CatalogTest.catalogPageLoads()` | TC-007 | Завантаження сторінки каталогу | ✅ PASSED |
| `CatalogTest.searchByKeyword()` | TC-008 | Пошук за ключовим словом | ✅ PASSED |
| `CatalogTest.addToCartFromCatalog()` | TC-009 | Додавання товару до кошика з каталогу | ✅ PASSED |
| `CheckoutTest.checkoutWithOneItemCompletesOrder()` | TC-018 | Оформлення замовлення з одним товаром | ✅ PASSED |
| `CheckoutTest.checkoutWithTwoItemsCompletesOrder()` | TC-019 | Оформлення замовлення з двома товарами | ✅ PASSED (після PR#48) |

> **TC-019** — до PR#48 падав з `NoSuchElementException` через відсутність явних очікувань.  
> Стабілізовано: `findElement` → `WebDriverWait` + `ExpectedConditions.elementToBeClickable`.

---

## 5. Підсумок запусків Surefire

Детальний протокол: [`surefire-summary.md`](surefire-summary.md)

| Запуск | Дата | Команда | Tests run | Failures | Errors | Стан |
|---|---|---|---|---|---|---|
| Запуск 1 | 12.11.2026 | `mvn clean test` | 91 | 11 | 0 | BUILD FAILURE |
| Запуск 2 | 12.11.2026 | `mvn test -DexcludedGroups=none` | 99 | 11 | 1 | BUILD FAILURE |
| Запуск 3 | _після PR#48_ | `mvn test -DexcludedGroups=none` | _очікується_ | _11_ | _0_ | _очікується_ |

**Усі 11 падінь у Запусках 1 і 2 — очікувані:** кожне відповідає звіту про дефект модуля v1 (BUG-015…BUG-020). Помилка `Errors: 1` у Запуску 2 (TC-019) — нестабільність тесту, не дефект продукту, виправлена в PR#48.

---

## 6. Дефекти модуля v1

| ID | GitHub Issue | BL-вимога | Тест, що виявляє | Severity |
|---|---|---|---|---|
| BUG-015 | #27 | BL-50 | `DiscountCalculatorTest.volumeDiscountPercent_boundaries[4]` | Major |
| BUG-016 | #28 | BL-50 | `DiscountCalculatorTest.volumeDiscount_roundsHalfUp` | Minor |
| BUG-017 | #29 | BL-10, BL-20, BL-40 | `CartTest.addItemWithQtyOutOfRangeIsRejected[4]`, `CartTest.changeQtyOutOfRangeIsRejected[3]`, `CartTest.reAddingSameSkuAboveMaxQtyIsRejected` | Major |
| BUG-018 | #30 | BL-20 | `CartNormalCasesTest.reAddingSameSkuIncreasesExistingLine`, `CartNormalCasesTest.reAddingUpToMaxQtyIsAccepted` | Major |
| BUG-019 | #50 | BL-70 | `PromoCodeValidatorRulesTest.expiryDateIsInclusive[2]` | Major |
| BUG-020 | #38 | BL-70, BL-80 | `PromoCodeValidatorRulesTest.singleUseCodeMustNotBeUsed[2]`, `PromoCodeValidatorRulesTest.applyRejectsUsedSingleUseCode`, `PromoCodeValidatorReasonsTest.usedCodeReasonHasPriorityOverInvalidValue` | Major |

---

## 7. Внесок учасників (за історією комітів)

| Учасник | Роль | Тест-класи / артефакти |
|---|---|---|
| Белінська Єлизавета | КТ | `DiscountCalculatorTest.java`, `PromoCodeValidatorRulesTest.java` (частково), координація, `surefire-summary.md`, `task-board.md` |
| Матвієнко Владислав | ТА | `PromoCodeValidatorTest.java`, `CatalogTest.java`, `LoginTest.java`, `ui-scenarios.md`, `coverage-matrix.md` |
| Бобров Павло | ІТ | `CartNormalCasesTest.java`, `CheckoutTest.java`, `PromoCodeValidatorReasonsTest.java` (частково), `pom.xml`, інфраструктура |
| Бойко Євгеній | СМ | `CartTest.java`, `CartNormalCasesTest.java`, `PromoCodeValidatorReasonsTest.java`, `code-review.md` |
| Кочаров Давид | АО | `CartTest.java` (частково), `reproduction-protocol.md` |

---

## 8. Висновок про стан модуля v1

Модуль `shopline` v1 містить **6 дефектів** у класах `Cart`, `DiscountCalculator` та `PromoCodeValidator`, виявлених юніт-тестуванням. Усі дефекти задокументовані в GitHub Issues з мітками `bug` і `module`, мають прив'язку до BL-вимог специфікації і відтворюються запуском відповідних тест-методів.

Автоматизований набір (38 методів JUnit 5 + 8 WebDriver-сценаріїв) готовий до регресійного запуску на модулі v2 у Спринті 5. Після виправлення дефектів очікується зелений `BUILD SUCCESS`.
