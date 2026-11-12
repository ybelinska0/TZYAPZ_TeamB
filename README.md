# TZYAPZ_TeamB
«ШопЛайн» — інтернет-магазин інструментів і садового обладнання

Спринт 4 «Автоматизація: JUnit 5 і Selenium WebDriver» — репозиторій автотестів модуля бізнес-логіки (кошик, знижки, промокоди) та UI-сценаріїв.

## Вимоги до середовища

| Інструмент | Версія |
|---|---|
| JDK | 17 (Temurin) |
| Maven | 3.9+ |
| Браузер | Chrome (драйвер ставить Selenium Manager автоматично) |

Перевірка: `mvn -v` має показувати Java 17.

## Збірка і запуск тестів

```bash
# юніт-тести (UI-тести з тегом "ui" пропускаються — так налаштовано Surefire)
mvn test

# усі тести, включно з UI (Selenium WebDriver, headless Chrome)
mvn test -DexcludedGroups=none

# UI-тести з видимим браузером (для демо)
mvn test -DexcludedGroups=none -Dheadless=false

# чиста збірка
mvn clean test
```

Частина юніт-тестів очікувано **падає на модулі v1** — це тести, що виявили заплановані дефекти; кожен такий тест має в коментарі ID звіту (BUG-015, BUG-016, BUG-018, BUG-020 — див. [Issues з міткою `module`](../../issues?q=is%3Aissue+label%3Amodule)).

UI-тести виконуються на офіційному demo-застосунку курсу [Sauce Demo](https://www.saucedemo.com/) (`standard_user` / `secret_sauce`).

## Структура репозиторію

```
pom.xml                                  # Maven: JDK 17, JUnit 5.10, Selenium 4.21, Surefire
docs/
  sprint1..sprint3/                      # артефакти попередніх спринтів
  sprint4/
    coverage-matrix.md                   # матриця «BL-вимога → правило → тести»
    task-board.md                        # розподіл задач спринта 4
  team/charter.md                        # чартер команди, ротація ролей
selenium-ide/
  shopline.side                          # проєкт Selenium IDE (записані сценарії)
  export/                                # експортовані Java-чернетки (довідково, не збираються)
src/
  main/java/ua/kpi/tzyapz/shopline/      # модуль продукту v1: Cart, DiscountCalculator,
                                         # PromoCode, PromoCodeValidator
  test/java/ua/kpi/tzyapz/shopline/      # юніт-тести (по класах модуля)
  test/java/ua/kpi/tzyapz/shopline/ui/   # WebDriver-тести (тег "ui")
```

## Автори тестів (спринт 4)

| Тест-клас | Що покриває | Автор |
|---|---|---|
| `DiscountCalculatorTest` | BL-50/BL-60: межі тарифів знижки, округлення, доставка, винятки | Белінська Єлизавета (КТ) |
| `CartTest` | BL-10/20/40: заборонені входи та винятки Cart | Кочаров Давид (АО) |
| `CartNormalCasesTest` | BL-10/20/40: допустимі значення, межі кількості, округлення ціни | Бобров Павло (ІТ) |
| `PromoCodeValidatorTest` | BL-70: формат промокоду (довжина 6–10, символи, null) | Матвієнко Владислав (ТА) |
| `PromoCodeValidatorRulesTest` | BL-70/80: строк дії, одноразовість, межі значення, сума знижки | Бойко Євгеній (СМ) |
| `PromoCodeValidatorReasonsTest` | BL-70/80: причини відмови та їх пріоритет, apply | Бобров Павло (ІТ) |
| `ui/LoginTest`, `ui/CheckoutTest`, `ui/CatalogTest` (+ `UiTestBase`) | TC-004/005/007/012/013/018/019/024 зі спринта 3 | Бобров Павло (ІТ) |

Разом: 38 юніт-тестів і 8 WebDriver-тестів.

## Процес

- Зміни — тільки через гілку + Pull Request; автор не рев'ює власний PR (чартер, п. 2).
- Дефекти модуля — GitHub Issues за шаблоном «Звіт про дефект», мітка `module`.
- Протокол запусків — `docs/sprint4/surefire-summary.md`.