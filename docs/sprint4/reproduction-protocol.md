# Протокол відтворення «з нуля» — Спринт 4

**Продукт:** ШопЛайн · **Спринт:** 4 «Автоматизація: JUnit 5 і Selenium WebDriver»
**Задача:** TTS-67
**Виконав:** Кочаров Давид (Аудитор-опонент, С4)
**Дата:** 12.11.2026

## 1. Середовище

| Параметр | Значення |
|---|---|
| ОС | Windows 11 (amd64), локаль `uk_UA`, кодування консолі Cp1251 |
| JDK | Temurin 17.0.21+9 |
| Maven | 3.10.0 |
| Браузер | Chrome 157.0.8103.52 (драйвер — Selenium Manager) |
| Стан репозиторію | `main`, коміт `cb286cc` (12.11.2026 13:12) |

Для порівняння: протокол Surefire (КТ) виконано на macOS, Maven 3.9, коміт `9afaec7`. Між `9afaec7` і `cb286cc` змінено лише `docs/sprint4/surefire-summary.md` — код і тести ті самі.

## 2. Кроки за README (виконано буквально)

| № | Крок README | Результат | Що довелося з'ясовувати самостійно |
|---|---|---|---|
| 1 | Встановити JDK 17, Maven 3.9+, Chrome; `mvn -v` показує Java 17 | виконано після встановлення | README називає версії, але не каже, як встановити JDK/Maven і додати Maven у `PATH` (на чистій машині Windows їх не було) |
| 2 | `git clone` у порожню папку | виконано | — |
| 3 | `mvn test` / `mvn clean test` | виконано, 15,5 с | У консолі Windows (Cp1251) кирилиця в повідомленнях assert-ів нечитабельна; виправляється `chcp 65001`. На результат не впливає (`pom.xml` задає UTF-8) |
| 4 | `mvn test -DexcludedGroups=none` | виконано, 19,7 с | — |

Висновок: проєкт **збирається і запускається за README без змін у коді чи конфігурації**.

## 3. Звірка з протоколом Surefire (`docs/sprint4/surefire-summary.md`)

| Запуск | Протокол КТ | Відтворення АО | Збіг |
|---|---|---|---|
| `mvn clean test` | Tests run: 91, Failures: 11, Errors: 0 | Tests run: 91, Failures: 11, Errors: 0 | так |
| `mvn test -DexcludedGroups=none` | Tests run: 99, Failures: 11, **Errors: 1** (TC-019) | Tests run: 99, Failures: 11, **Errors: 0** | ні, розбіжність |
| UI-тести | 7/8 (TC-019 — `NoSuchElementException: #checkout`) | **8/8** | ні, розбіжність |

Перелік 11 червоних юніт-тестів збігся повністю (див. розділ 4.2).

**Розбіжність TC-019.** `CheckoutTest.java` не змінювався з 11.11 (`7335d9e`): у рядку 55 `driver.findElement(By.id("checkout")).click()` досі без явного очікування, задачу стабілізації S4-19 не виконано. Те, що тест у мене пройшов, а в КТ впав, підтверджує: **тест нестабільний (flaky)**, а не виправлений. Норматив «≥8 UI-тестів» виконується лише за умови, що TC-019 проходить стабільно.

Попередження `Unable to find CDP implementation matching 157` повторюється й у мене — некритичне, на результат не впливає (як і зазначено в протоколі КТ).

## 4. Простежуваність

### 4.1. Тест → BL-вимога

| Тест-клас | Тест-методів | BL-ID у `@DisplayName` | Автор (за історією комітів) |
|---|---|---|---|
| `DiscountCalculatorTest` | 4 | так: BL-50, BL-60 | Белінська Є. |
| `CartTest` | 7 | так: BL-10, BL-20, BL-40 | Кочаров Д. |
| `CartNormalCasesTest` | 8 | так: BL-10, BL-20, BL-40 | Бобров П. |
| `PromoCodeValidatorTest` | 4 | так: BL-70 | Матвієнко В. |
| `PromoCodeValidatorRulesTest` | 10 | так: BL-70, BL-80 | Бойко Є. |
| `PromoCodeValidatorReasonsTest` | 5 | так: BL-70, BL-80 | Бобров П. |
| **Разом** | **38** (91 запуск із параметризацією) | 38 з 38 | ≥3 на кожного учасника — виконано |

Кожен тест-метод має BL-ID. **Але матриця покриття `docs/sprint4/coverage-matrix.md` застаріла** (останній коміт 09.11, `3528dfa`): у ній 3 рядки, лише тести Матвієнка, під хибним ID BL-50 (промокоди — BL-70/80), з довжиною коду 5–10 замість 6–10 за SRS-1.1, і з назвами методів (`nullOrBlankCodeThrows`, `validFormatReturnsTrue`, `invalidFormatThrows`), яких у коді вже немає. 35 з 38 тестів і вимоги BL-10, 20, 40, 50, 60, 80 у матриці відсутні.

### 4.2. Червоний тест → дефект

| Тест (failed) | ID у коментарі тесту | У протоколі Surefire |
|---|---|---|
| `DiscountCalculatorTest.volumeDiscountPercent_boundaries[4]` | немає | BUG-015 |
| `DiscountCalculatorTest.volumeDiscount_roundsHalfUp` | немає | BUG-016 |
| `CartTest.addItemWithQtyOutOfRangeIsRejected[4]` | BUG-017 | BUG-017 |
| `CartTest.changeQtyOutOfRangeIsRejected[3]` | BUG-017 | BUG-017 |
| `CartTest.reAddingSameSkuAboveMaxQtyIsRejected` | BUG-018 | BUG-017/018 (розбіжність) |
| `CartNormalCasesTest.reAddingSameSkuIncreasesExistingLine` | BUG-018 | BUG-018 |
| `CartNormalCasesTest.reAddingUpToMaxQtyIsAccepted` | BUG-018 | BUG-018 |
| `PromoCodeValidatorRulesTest.expiryDateIsInclusive[2]` | BUG-019 | BUG-019 |
| `PromoCodeValidatorRulesTest.singleUseCodeMustNotBeUsed[2]` | BUG-020 | BUG-020 |
| `PromoCodeValidatorRulesTest.applyRejectsUsedSingleUseCode` | BUG-020 | BUG-020 |
| `PromoCodeValidatorReasonsTest.usedCodeReasonHasPriorityOverInvalidValue` | BUG-020 | BUG-020 |

Знайдено 6 різних дефектів модуля (BUG-015…020) при нормативі ≥5 з 6 закладених — виконано.

### 4.3. UI-тест → тест-кейс спринта 3

| Тест-клас | TC-ID у `@DisplayName` |
|---|---|
| `LoginTest` | TC-004, TC-005, TC-024 |
| `CatalogTest` | TC-007, TC-012, TC-013 |
| `CheckoutTest` | TC-018, TC-019 |

Усі 8 мають ID тест-кейсу — виконано. Selenium IDE: `selenium-ide/shopline.side` — 2 сценарії (TC-004, TC-019), експорт у `selenium-ide/export/` — виконано.

### 4.4. Звіт про дефект → тест

Перевірено в GitHub Issues (мітка `module`): кожен звіт має посилатися на тест-клас і метод, що його виявив.

| Звіт | Тест(и), що його виявили | Посилання на тест у звіті |
|---|---|---|
| BUG-015 | `DiscountCalculatorTest.volumeDiscountPercent_boundaries[4]` | |
| BUG-016 | `DiscountCalculatorTest.volumeDiscount_roundsHalfUp` | |
| BUG-017 | `CartTest.addItemWithQtyOutOfRangeIsRejected[4]`, `CartTest.changeQtyOutOfRangeIsRejected[3]` | |
| BUG-018 | `CartTest.reAddingSameSkuAboveMaxQtyIsRejected`, `CartNormalCasesTest.reAddingSameSkuIncreasesExistingLine`, `CartNormalCasesTest.reAddingUpToMaxQtyIsAccepted` | |
| BUG-019 | `PromoCodeValidatorRulesTest.expiryDateIsInclusive[2]` | |
| BUG-020 | `PromoCodeValidatorRulesTest.singleUseCodeMustNotBeUsed[2]`, `PromoCodeValidatorRulesTest.applyRejectsUsedSingleUseCode`, `PromoCodeValidatorReasonsTest.usedCodeReasonHasPriorityOverInvalidValue` | |

## 5. Зауваження

| № | Зауваження | Вплив | Відповідальний |
|---|---|---|---|
| 1 | `coverage-matrix.md` застаріла: 3 рядки з 38 тестів, хибний BL-ID, довжина 5–10 замість 6–10, неіснуючі назви методів | Високий — обов'язковий артефакт не відповідає коду | Матвієнко В. (ТА) |
| 2 | TC-019 нестабільний: `findElement(By.id("checkout"))` без очікування (CheckoutTest.java:55), S4-19 не виконано | Середній — норматив UI 8/8 під ризиком на демо | Бобров П. (ІТ) |
| 3 | `DiscountCalculatorTest`: у червоних тестах немає коментарів BUG-015 і BUG-016 (п. 8 чек-листа review) | Середній | Белінська Є. (автор), Бойко Є. (review пропустив) |
| 4 | Протокол Surefire: для `CartTest.reAddingSameSkuAboveMaxQtyIsRejected` вказано «BUG-017/018», у коді — BUG-018; один тест — один ID | Низький | Белінська Є. (КТ) |
| 5 | README перелічує дефекти «BUG-015, 016, 018, 020» — пропущено BUG-017 і BUG-019 | Низький | Белінська Є. (КТ) |
| 6 | README: «38 юніт-тестів» без пояснення, що Surefire показує 91 запуск (параметризація) — читач ззовні бачить розбіжність | Низький | Белінська Є. (КТ) |
| 7 | README: розділ «Структура» не згадує `docs/sprint4/surefire-summary.md` | Низький | Белінська Є. (КТ) |
| 8 | README не описує встановлення JDK/Maven і проблему кодування консолі Windows (`chcp 65001`) | Низький | Белінська Є. (КТ), Бобров П. (ІТ) |

## 6. Очікувані питання замовника на демо

| № | Питання | Хто відповідає |
|---|---|---|
| 1 | 11 тестів червоні. Як довести, що це дефекти модуля, а не помилки в тестах? | автор відповідного тесту (очікуване значення — з тексту BL-вимоги SRS-1.1) |
| 2 | BL-50: що повертає `volumeDiscountPercent` для 1999.99, 2000.00, 4999.99, 5000.00? Чому саме ці значення? | Белінська Є. |
| 3 | 150.005 → 150.01: чому саме HALF_UP і як це перевірено? | Белінська Є. |
| 4 | Чому кількість 0 і 100 відхиляються, а 1 і 99 приймаються? Де межі в коді тестів? | Кочаров Д., Бобров П. |
| 5 | BUG-018 ловлять три тести, BUG-020 — чотири. Це не дублювання? | автори відповідних тестів |
| 6 | TC-019 у протоколі КТ впав, у протоколі АО пройшов. Чи можна довіряти UI-набору і що зроблено для стабільності? | Бобров П. |
| 7 | Чому UI-тести виконано на Sauce Demo, а не на Toolshop? Як тест-кейси спринта 3 перенесено на інший застосунок? | Бобров П., Матвієнко В. |
| 8 | BL-80 вимагає не приймати код, використаний в інших магазинах. Як це перевіряється в бібліотеці без зовнішніх залежностей? | Бойко Є., Бобров П. |
| 9 | README каже «38 юніт-тестів», Surefire — «Tests run: 91». Звідки різниця? | Белінська Є. |
| 10 | Що станеться з червоними тестами, коли модуль виправлять (v2)? Чи треба буде змінювати тести? | будь-хто з команди |

## 7. Висновок

Проєкт **відтворюється «з нуля»** на іншій ОС (Windows 11) і іншій версії Maven за README без змін коду. Юніт-результат (91 / 11 Failures) повністю збігається з протоколом Surefire; усі 11 червоних тестів — очікувані й відповідають дефектам BUG-015…020. Юніт-норматив (38 ≥ 20, ≥3 на кожного) і норматив дефектів (6 ≥ 5) виконано.
