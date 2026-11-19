# TZYAPZ_TeamB
«ШопЛайн» — інтернет-магазин інструментів і садового обладнання

Спринт 4 «Автоматизація: JUnit 5 і Selenium WebDriver» — репозиторій автотестів модуля бізнес-логіки (кошик, знижки, промокоди) та UI-сценаріїв.

## Вимоги до середовища

| Інструмент | Версія |
|---|---|
| JDK | 17 (Temurin) |
| Maven | 3.9+ |
| Браузер | Chrome (драйвер ставить Selenium Manager автоматично; потрібен інтернет під час першого запуску) |

Перевірка: `mvn -v` має показувати Java 17.

**Чиста машина (Windows):** встановити Temurin JDK 17 (https://adoptium.net) і Maven (https://maven.apache.org/download.cgi), додати `bin` Maven у змінну `PATH`, перезапустити термінал і перевірити `mvn -v`. Якщо в консолі Windows кирилиця в повідомленнях тестів нечитабельна, перед запуском виконайте `chcp 65001`. На результат тестів це не впливає.

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

# один UI-тест з видимим браузером (демо, заняття 2)
mvn test -DexcludedGroups=none -Dheadless=false -Dtest=LoginTest
```

**Очікуваний результат на модулі v1:** `mvn clean test` → `Tests run: 96, Failures: 11, Errors: 0` (BUILD FAILURE). З UI-тестами (`-DexcludedGroups=none`) → `Tests run: 104, Failures: 11, Errors: 0`.

- Юніт-тест-методів **41**. Surefire показує **96** запусків, бо кожен рядок параметризованого тесту (`@CsvSource`, `@ValueSource`) — окремий запуск, а ще 2 запуски дає `ExampleTest` зі стартового проєкту.
- **11 червоних тестів — очікувані:** це тести, що виявили закладені дефекти модуля v1. Кожен має в коментарі ID звіту: **BUG-015, BUG-016, BUG-017, BUG-018, BUG-019, BUG-020**. Див. [Issues з міткою `module`](../../issues?q=is%3Aissue+label%3Amodule).

UI-тести виконуються на резервному demo-застосунку курсу [Sauce Demo](https://www.saucedemo.com/) (`standard_user` / `secret_sauce`). **Дозвіл викладача на використання Sauce Demo як UI-резерву отримано** — ризик щодо нормативу ≥ 8 UI-тестів знято.

## Посилання

| Що | Де |
|---|---|
| Репозиторій | https://github.com/ybelinska0/TZYAPZ_TeamB |
| Трекер дефектів модуля (GitHub Issues, мітка `module`) | https://github.com/ybelinska0/TZYAPZ_TeamB/issues?q=label%3Amodule |
| Борд задач (Jira) | https://shopline1teamb.atlassian.net/jira/software/projects/TTS |
| Матриця покриття | [`docs/sprint4/coverage-matrix.md`](docs/sprint4/coverage-matrix.md) |
| Протокол запуску Surefire | [`docs/sprint4/surefire-summary.md`](docs/sprint4/surefire-summary.md) |
| Розділ «Автоматизація» досьє | [`docs/sprint4/automation-dossier.md`](docs/sprint4/automation-dossier.md) |

## Структура репозиторію

```
pom.xml                                  # Maven: JDK 17, JUnit 5.10, Selenium 4.21, Surefire
docs/
  sprint1..sprint3/                      # артефакти попередніх спринтів
  sprint4/
    automation-dossier.md                # розділ «Автоматизація» досьє якості v4 (норматив, дефекти, висновок)
    coverage-matrix.md                   # матриця «BL-вимога → правило → тести → рецензент»
    ui-scenarios.md                      # TC-xx → кроки, локатори, очікування, тест-метод, адаптація
    surefire-summary.md                  # протокол запуску Surefire: дата, коміт, команда, результати
    code-review.md                       # журнал code review тест-класів за чек-листом 5.3
    reproduction-protocol.md             # протокол відтворення «з нуля» Аудитора-опонента
    task-board.md                        # розподіл задач спринта 4
  team/charter.md                        # чартер команди, ротація ролей
selenium-ide/
  shopline.side                          # проєкт Selenium IDE (записані сценарії TC-004, TC-019)
  export/                                # експортовані Java-чернетки (довідково, не збираються)
src/
  main/java/ua/kpi/tzyapz/shopline/      # модуль продукту v1: Cart, DiscountCalculator,
                                         # PromoCode, PromoCodeValidator
  test/java/ua/kpi/tzyapz/shopline/      # юніт-тести (по класах модуля)
  test/java/ua/kpi/tzyapz/shopline/ui/   # WebDriver-тести (тег "ui")
```

Звіти Surefire після запуску лежать у `target/surefire-reports/` (`*.txt` — підсумок по класу, `TEST-*.xml` — усі тести з назвами `@DisplayName`).

## Автори тестів (спринт 4)

| Тест-клас | Що покриває | Автор |
|---|---|---|
| `DiscountCalculatorTest` | BL-50/BL-60: межі тарифів знижки, нормальні випадки, округлення, доставка | Белінська Єлизавета (КТ) — 5 методів |
| `DiscountCalculatorTest` (від'ємні входи) | BL-50/BL-60: `IllegalArgumentException` для `volumeDiscount` і `shippingFee` | Бойко Євгеній (СМ) — 2 методи |
| `CartTest` | BL-10/20/40: заборонені входи та винятки Cart | Кочаров Давид (АО) — 7 методів |
| `CartNormalCasesTest` | BL-10/20/40: допустимі значення, межі кількості, округлення ціни | Бобров Павло (ІТ) — 8 методів |
| `PromoCodeValidatorTest` | BL-70: формат промокоду (довжина 6–10, символи, null) | Матвієнко Владислав (ТА) — 4 методи |
| `PromoCodeValidatorRulesTest` | BL-70/80: строк дії, одноразовість, межі значення, сума знижки | Бойко Євгеній (СМ) — 10 методів |
| `PromoCodeValidatorReasonsTest` | BL-70/80: причини відмови та їх пріоритет, apply | Бобров Павло (ІТ) — 5 методів |
| `ui/LoginTest`, `ui/CheckoutTest`, `ui/CatalogTest` (+ `UiTestBase`) | TC-004/005/007/012/013/018/019/024 зі спринта 3 | Бобров Павло (ІТ) |

Разом: 41 юніт-тест (Белінська 5, Кочаров 7, Бойко 12, Матвієнко 4, Бобров 13) і 8 WebDriver-тестів.

## Процес

- Зміни — тільки через гілку + Pull Request; автор не рев'ює власний PR (чартер, п. 2).
- Дефекти модуля — GitHub Issues за шаблоном «Звіт про дефект», мітка `module`.
- Протокол запусків — `docs/sprint4/surefire-summary.md`.
