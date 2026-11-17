# Протокол відтворення «з нуля» — Спринт 4

**Продукт:** ШопЛайн · **Спринт:** 4 «Автоматизація: JUnit 5 і Selenium WebDriver»
**Задача:** TTS-67
**Виконав:** Кочаров Давид (Аудитор-опонент, С4)
**Дата:** 12.11.2026, 13:20–13:26 (оновлено 16.11.2026: заповнено §4.4, додано статус зауважень у §5; оновлено 17.11.2026: статуси §4.2, §4.4, §5, повторне відтворення на фінальному коміті — §8)

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

Таблиця — стан на 12.11 (`cb286cc`). На фінальному коміті (§8): 41 метод — `DiscountCalculatorTest` 7 (+2 від'ємні входи Бойка Є., PR #57; +1 нормальні випадки Белінської Є.), Бойко Є. — 12.

Кожен тест-метод має BL-ID. **Але матриця покриття `docs/sprint4/coverage-matrix.md` застаріла** (останній коміт 09.11, `3528dfa`): у ній 3 рядки, лише тести Матвієнка, під хибним ID BL-50 (промокоди — BL-70/80), з довжиною коду 5–10 замість 6–10 за SRS-1.1, і з назвами методів (`nullOrBlankCodeThrows`, `validFormatReturnsTrue`, `invalidFormatThrows`), яких у коді вже немає. 35 з 38 тестів і вимоги BL-10, 20, 40, 50, 60, 80 у матриці відсутні.

### 4.2. Червоний тест → дефект

| Тест (failed) | ID у коментарі тесту | У протоколі Surefire |
|---|---|---|
| `DiscountCalculatorTest.volumeDiscountPercent_boundaries[4]` | немає → BUG-015 (додано в PR #47) | BUG-015 |
| `DiscountCalculatorTest.volumeDiscount_roundsHalfUp` | немає → BUG-016 (додано в PR #47) | BUG-016 |
| `CartTest.addItemWithQtyOutOfRangeIsRejected[4]` | BUG-017 | BUG-017 |
| `CartTest.changeQtyOutOfRangeIsRejected[3]` | BUG-017 | BUG-017 |
| `CartTest.reAddingSameSkuAboveMaxQtyIsRejected` | BUG-018 | BUG-017 → BUG-018 (виправлено в PR #54) |
| `CartNormalCasesTest.reAddingSameSkuIncreasesExistingLine` | BUG-018 | BUG-018 |
| `CartNormalCasesTest.reAddingUpToMaxQtyIsAccepted` | BUG-018 | BUG-018 |
| `PromoCodeValidatorRulesTest.expiryDateIsInclusive[2]` | BUG-019 | BUG-019 |
| `PromoCodeValidatorRulesTest.singleUseCodeMustNotBeUsed[2]` | BUG-020 | BUG-020 |
| `PromoCodeValidatorRulesTest.applyRejectsUsedSingleUseCode` | BUG-020 | BUG-020 |
| `PromoCodeValidatorReasonsTest.usedCodeReasonHasPriorityOverInvalidValue` | BUG-020 | BUG-020 |

Стан на фінальному коміті (§8): усі 11 червоних тестів мають ID у коментарі, і він збігається з протоколом Surefire.

Знайдено 6 різних дефектів модуля (BUG-015…020) при нормативі ≥5 з 6 закладених — виконано.

### 4.3. UI-тест → тест-кейс спринта 3

| Тест-клас | TC-ID у `@DisplayName` |
|---|---|
| `LoginTest` | TC-004, TC-005, TC-024 |
| `CatalogTest` | TC-007, TC-012, TC-013 |
| `CheckoutTest` | TC-018, TC-019 |

Усі 8 мають ID тест-кейсу — виконано. Selenium IDE: `selenium-ide/shopline.side` — 2 сценарії (TC-004, TC-019), експорт у `selenium-ide/export/` — виконано.

### 4.4. Звіт про дефект → тест

Для кожного звіту знайдено тест-методи, що його виявили: за коментарем `// BUG-NNN` у тест-класі (номер рядка коментаря) і за таблицею failed запуску 4 у `docs/sprint4/surefire-summary.md`.

| Звіт | Тест(и), що його виявили | Посилання на тест (коментар у коді · surefire-summary.md) |
|---|---|---|
| BUG-015 | `DiscountCalculatorTest.volumeDiscountPercent_boundaries[4]` | `DiscountCalculatorTest#volumeDiscountPercent_boundaries` (DiscountCalculatorTest.java:32) · запуск 4 |
| BUG-016 | `DiscountCalculatorTest.volumeDiscount_roundsHalfUp` | `DiscountCalculatorTest#volumeDiscount_roundsHalfUp` (DiscountCalculatorTest.java:48) · запуск 4 |
| BUG-017 | `CartTest.addItemWithQtyOutOfRangeIsRejected[4]`, `CartTest.changeQtyOutOfRangeIsRejected[3]` | `CartTest#addItemWithQtyOutOfRangeIsRejected` (CartTest.java:42) · запуск 4; `CartTest#changeQtyOutOfRangeIsRejected` (CartTest.java:79) · запуск 4 |
| BUG-018 | `CartTest.reAddingSameSkuAboveMaxQtyIsRejected`, `CartNormalCasesTest.reAddingSameSkuIncreasesExistingLine`, `CartNormalCasesTest.reAddingUpToMaxQtyIsAccepted` | `CartTest#reAddingSameSkuAboveMaxQtyIsRejected` (CartTest.java:70) · запуск 4; `CartNormalCasesTest#reAddingSameSkuIncreasesExistingLine` (CartNormalCasesTest.java:67) · запуск 4; `CartNormalCasesTest#reAddingUpToMaxQtyIsAccepted` (CartNormalCasesTest.java:81) · запуск 4 |
| BUG-019 | `PromoCodeValidatorRulesTest.expiryDateIsInclusive[2]` | `PromoCodeValidatorRulesTest#expiryDateIsInclusive` (PromoCodeValidatorRulesTest.java:60) · запуск 4 |
| BUG-020 | `PromoCodeValidatorRulesTest.singleUseCodeMustNotBeUsed[2]`, `PromoCodeValidatorRulesTest.applyRejectsUsedSingleUseCode`, `PromoCodeValidatorReasonsTest.usedCodeReasonHasPriorityOverInvalidValue` | `PromoCodeValidatorRulesTest#singleUseCodeMustNotBeUsed` (PromoCodeValidatorRulesTest.java:80) · запуск 4; `PromoCodeValidatorRulesTest#applyRejectsUsedSingleUseCode` (PromoCodeValidatorRulesTest.java:183) · запуск 4; `PromoCodeValidatorReasonsTest#usedCodeReasonHasPriorityOverInvalidValue` (PromoCodeValidatorReasonsTest.java:70) · запуск 4 |

## 5. Зауваження

| № | Зауваження | Вплив | Відповідальний | Статус на 16.11 |
|---|---|---|---|---|
| 1 | `coverage-matrix.md` застаріла: 3 рядки з 38 тестів, хибний BL-ID, довжина 5–10 замість 6–10, неіснуючі назви методів | Високий — обов'язковий артефакт не відповідає коду | Матвієнко В. (ТА) | Виправлено 13.11 (PR #45): усі 38 тестів, BL-10…80, довжина 6–10 |
| 2 | TC-019 нестабільний: `findElement(By.id("checkout"))` без очікування (CheckoutTest.java:55), S4-19 не виконано | Середній — норматив UI 8/8 під ризиком на демо | Бобров П. (ІТ) | Виправлено в коді 13.11 (PR #48); протокол Surefire доповнено запусками 3 і 5 (8/8); підтверджено повторним відтворенням (§8) |
| 3 | `DiscountCalculatorTest`: у червоних тестах немає коментарів BUG-015 і BUG-016 (п. 8 чек-листа review) | Середній | Белінська Є. (автор), Бойко Є. (review пропустив) | Виправлено 13.11 (PR #47) |
| 4 | Протокол Surefire: для `CartTest.reAddingSameSkuAboveMaxQtyIsRejected` вказано «BUG-017», у коді — BUG-018 (тест ловить заміну рядка при повторному додаванні, тобто BUG-018); один тест — один ID | Низький | Белінська Є. (КТ) | Виправлено 16.11 (PR #54) |
| 5 | README перелічує дефекти «BUG-015, 016, 018, 020» — пропущено BUG-017 і BUG-019 | Низький | Белінська Є. (КТ) | Виправлено 17.11.2026 (PR Белінської Є. з README, рецензент Бойко Є.) |
| 6 | README: «38 юніт-тестів» без пояснення, що Surefire показує 91 запуск (параметризація) — читач ззовні бачить розбіжність | Низький | Белінська Є. (КТ) | Виправлено 17.11.2026 (PR Белінської Є. з README, рецензент Бойко Є.) |
| 7 | README: розділ «Структура» не згадує `docs/sprint4/surefire-summary.md` | Низький | Белінська Є. (КТ) | Виправлено 17.11.2026 (PR Белінської Є. з README, рецензент Бойко Є.) |
| 8 | README не описує встановлення JDK/Maven і проблему кодування консолі Windows (`chcp 65001`) | Низький | Белінська Є. (КТ), Бобров П. (ІТ) | Виправлено 17.11.2026 (PR Белінської Є. з README, рецензент Бойко Є.) |

## 6. Очікувані питання замовника на демо

| № | Питання | Хто відповідає |
|---|---|---|
| 1 | 11 тестів червоні. Як довести, що це дефекти модуля, а не помилки в тестах? | автор відповідного тесту (очікуване значення — з тексту BL-вимоги SRS-1.1) |
| 2 | BL-50: що повертає `volumeDiscountPercent` для 1999.99, 2000.00, 4999.99, 5000.00? Чому саме ці значення? | Белінська Є. |
| 3 | 150.005 → 150.01: чому саме HALF_UP і як це перевірено? | Белінська Є. |
| 4 | Чому кількість 0 і 100 відхиляються, а 1 і 99 приймаються? Де межі в коді тестів? | Кочаров Д., Бобров П. |
| 5 | BUG-018 ловлять три тести, BUG-020 — чотири. Це не дублювання? | автори відповідних тестів |
| 6 | TC-019 у протоколі КТ впав, у протоколі АО пройшов. Чи можна довіряти UI-набору і що зроблено для стабільності? | Бобров П. (PR #48: явні очікування; запуск 5 — двічі поспіль 8/8) |
| 7 | Чому UI-тести виконано на Sauce Demo, а не на Toolshop? Як тест-кейси спринта 3 перенесено на інший застосунок? | Бобров П., Матвієнко В. |
| 8 | BL-80 вимагає не приймати код, використаний в інших магазинах. Як це перевіряється в бібліотеці без зовнішніх залежностей? | Бойко Є., Бобров П. |
| 9 | README каже «41 юніт-тест», Surefire — «Tests run: 96». Звідки різниця? | Белінська Є. (параметризовані тести: кожен рядок даних — окремий запуск; + 2 запуски `ExampleTest`) |
| 10 | Що станеться з червоними тестами, коли модуль виправлять (v2)? Чи треба буде змінювати тести? | будь-хто з команди |
| 11 | TC-019 спринта 3 перевіряє доставку 120,00 і оплату «Bank Transfer», а UI-тест — податок Sauce Demo. Як тоді перевірено BL-60? | Матвієнко В., Бобров П. (`ui-scenarios.md`, колонка «Адаптація»; BL-60 — юніт-тести `shippingFee`) |

## 7. Висновок

Проєкт **відтворюється «з нуля»** на іншій ОС (Windows 11) і іншій версії Maven за README без змін коду. Юніт-результат (91 / 11 Failures) повністю збігається з протоколом Surefire; усі 11 червоних тестів — очікувані й відповідають дефектам BUG-015…020. Юніт-норматив (38 ≥ 20, ≥3 на кожного) і норматив дефектів (6 ≥ 5) виконано.

Стан на 17.11.2026: усі зауваження 1–8 виправлено; повторне відтворення на фінальному коміті — §8.

## 8. Повторне відтворення (17.11.2026)

### 8.1. Прогін 17.11.2026 — поточний `main`, до злиття виправлень після рев'ю

| Параметр | Значення |
|---|---|
| Дата | 17.11.2026, завершено о 12:05:45 |
| Стан репозиторію | `main`, коміт `de44a2f` (Merge pull request #52) — виправлення після рев'ю ще не злито |
| Середовище | Windows 11, JDK 17 (`release 17`), Maven 3.10.0, консоль PowerShell без `chcp 65001` |
| Папка | новий порожній каталог `audit-s4\TZYAPZ_TeamB`, `git clone` з нуля |

| № | Крок README | Результат | Збіг |
|---|---|---|---|
| 1 | `mvn clean test` | `Tests run: 93, Failures: 11, Errors: 0, Skipped: 0` → BUILD FAILURE, 5,5 с | так — очікувано для `de44a2f`: 91 запуск (протокол Surefire, запуск 1) + 2 тести PR #57 |
| 2 | Перелік 11 червоних тестів | `CartNormalCasesTest` 2, `CartTest` 3, `DiscountCalculatorTest` 2, `PromoCodeValidatorReasonsTest` 1, `PromoCodeValidatorRulesTest` 3 | так — збігся з §4.2 повністю (BUG-015…020) |
| 3 | Зелені класи | `PromoCodeValidatorTest` 13/13, `ExampleTest` 2/2; нові тести PR #57 зелені | так |
| 4 | `mvn test -DexcludedGroups=none` | не запускався в цьому прогоні — див. 8.2 | — |

**Розбіжність із протоколом Surefire:** у `surefire-summary.md` на `de44a2f` немає запуску з 93 тестами (останній юніт-запуск — 91 на `9afaec7`). Протокол не оновлено після PR #57. Передано КТ: запуск 4 протоколу Surefire має бути на фінальному коміті.

**Кодування консолі:** без `chcp 65001` кирилиця в повідомленнях assert-ів (`@DisplayName`, тексти `assertAll`) нечитабельна, як і 12.11 (зауваження №8). На результат не впливає; у виправленому README є пояснення.

### 8.2. Прогін на фінальному коміті — після злиття PR #59–#62

| Параметр | Значення |
|---|---|
| Дата | 17.11.2026, 21:44–21:45 |
| Стан репозиторію | `main`, коміт `bb0df4f` (Merge pull request #61) — останній коміт, що змінює код тестів; подальші PR змінюють лише документи |
| Середовище | Windows 11, JDK 17 (`release 17`), Maven 3.10.0, Chrome 157.0.8103.79 (драйвер — Selenium Manager), PowerShell без `chcp 65001` |
| Папка | `audit-s4\TZYAPZ_TeamB`, оновлено до `origin/main`; `mvn clean test` — збірка з нуля (`target/` видалено, перекомпільовано 4 класи модуля і 11 тест-класів) |

| № | Крок README | Результат | Збіг із протоколом Surefire |
|---|---|---|---|
| 1 | `git log -1 --oneline` | `bb0df4f (HEAD -> main, origin/main)` | — |
| 2 | `mvn clean test` | `Tests run: 96, Failures: 11, Errors: 0, Skipped: 0` → BUILD FAILURE, 4,95 с | так (запуск 4) |
| 3 | `mvn test -DexcludedGroups=none` | `Tests run: 104, Failures: 11, Errors: 0, Skipped: 0` → BUILD FAILURE, 23,4 с | так (запуск 5) |
| 4 | UI-класи | `CatalogTest` 3/3 (10,5 с), `CheckoutTest` 2/2 (3,5 с), `LoginTest` 3/3 (5,2 с) — **8/8** | так |
| 5 | Перелік 11 червоних тестів | ті самі, що в §4.2 і 8.1: `CartNormalCasesTest` 2, `CartTest` 3, `DiscountCalculatorTest` 2, `PromoCodeValidatorReasonsTest` 1, `PromoCodeValidatorRulesTest` 3 (BUG-015…020) | так |
| 6 | Зелені юніт-класи | `PromoCodeValidatorTest` 13/13, `ExampleTest` 2/2; новий `volumeDiscount_normalCases` зелений (`DiscountCalculatorTest` 16 запусків, 2 червоні — BUG-015, BUG-016) | так |

Попередження `Unable to find CDP implementation matching 157` повторюється для кожного UI-тесту. Воно некритичне, на результат не впливає (як у §3 і в протоколі Surefire). TC-012 після зміни очікування (PR #60) пройшов.

Що довелося з'ясовувати самостійно: нічого. Кирилиця в повідомленнях assert-ів без `chcp 65001` нечитабельна, але README тепер це пояснює.

**Висновок:** проєкт на фінальному коміті `bb0df4f` відтворюється за README на іншій машині (Windows 11) без змін коду і без підказок авторів. Юніт-набір — 96 / 11 / 0, повний набір — 104 / 11 / 0, UI 8/8. Усі 11 падінь — задокументовані дефекти модуля v1 BUG-015…020. Результат збігається з протоколом Surefire (запуски 4–5). Зауважень до відтворюваності немає.
