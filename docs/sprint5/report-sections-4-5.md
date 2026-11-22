# Звіт про якість продукту — розділи 4–5 (чернетка)

**Задача:** S5-19 (TTS-96) · **Автор:** Бобров Павло (СМ) · **Рецензент:** Кочаров Давид (КТ)  
**Стан:** чернетка від 22.11.2026. Дані спринтів 3–4 заповнено; місця `<…v2…>` заповнюються після регресії — з `regression-log.xlsx` (S5-07, S5-08, S5-10) і статусів дефектів (S5-11, S5-12). Для зшивання в `quality-report.docx` (S5-22).

---

## 4. Тест-кейси

### 4.1. Обсяг

У спринті 3 спроєктовано й прорецензовано **31 ручний тест-кейс** (TC-001…TC-031) для demo-застосунку Toolshop (`practicesoftwaretesting.com`) — повний перелік: [`docs/sprint3/test-cases.xlsx`](../sprint3/test-cases.xlsx).

| Ознака | Кількість |
| --- | --- |
| Пріоритет High / Medium | 23 / 8 |
| Позитивні / негативні | 20 / 11 |
| З них на граничні значення | 8 |
| Прорецензовано (статус `reviewed`) | 31 з 31 |

Техніки: класи еквівалентності й граничні значення (межі кількості 0 / 1 / 99 / 100, довжина пароля 7 / 8, довжина повідомлення 49 / 50), сценарні перевірки ключових процесів (реєстрація, вхід, пошук, кошик, оформлення замовлення, звернення).

### 4.2. Розподіл за розділами SRS 1.1

Кейс може перевіряти кілька вимог, тому сума кейсів за розділами більша за 31.

| Розділ SRS | Вимог | Покрито ручними кейсами (≥ 1 кейс) | Кейсів із посиланням на розділ |
| --- | --- | --- | --- |
| F — функціональні | 14 | 14 | 24 |
| GUI — інтерфейс | 11 | 9 | 14 |
| BL — бізнес-логіка | 7 | 4 (через інтерфейс: BL-10, 20, 40, 60) | 7 |
| P — продуктивність | 2 | 0 | 0 |
| S — захищеність | 2 | 2 | 2 |
| U — зручність | 3 | 2 | 2 |
| D — документація | 2 | 0 | 0 |
| **Разом** | **41** | **31 (75,6 %)** | — |

Ручні кейси свідомо не покривають BL-50, BL-70, BL-80 (модуль не інтегровано в інтерфейс — покрито юніт-тестами спринта 4, розділ 6), нефункціональні P-10, P-20, U-10 і документаційні D-10, D-20, а також GUI-80, GUI-100 (пороги — пропозиція, що чекає підтвердження замовника). Причини — аркуш `Coverage` у `test-cases.xlsx`; підсумкове покриття разом з автотестами — матриця простежуваності, розділ 8.

### 4.3. Приклад кейса

**TC-014 — Кількість 99 (верхня межа діапазону 1..99) приймається і додається в кошик**

| Поле | Значення |
| --- | --- |
| Вимоги | GUI-30, BL-10 |
| Пріоритет / тип | High / позитивний, граничне значення (b = 99) |
| Передумови | Chrome, нова сесія (інкогніто), вікно 1280×800; вхід не виконано; кошик порожній |
| Кроки | 1. Відкрити `https://practicesoftwaretesting.com`. 2. Ввести в пошук «Combination Pliers». 3. Натиснути «Search». 4. Відкрити картку товару. 5. Виділити вміст поля «Quantity». 6. Ввести `99`. 7. Натиснути «Add to cart». 8. Закрити вкладку |
| Очікуваний результат | Після кроку 6 поле містить 99 без повідомлень про помилку; після кроку 7 — повідомлення про додавання, лічильник кошика показує 99 |
| Автор / рецензент | Бобров П. / Кочаров Д., 28.10.2026 |
| Результат Run 1 (27.10.2026, Бойко Є.) | passed |
| Результат на v2 | `<з regression-log.xlsx: результат, дата, виконавець>` |

Кейс входить у трійку граничних кейсів GUI-30 / BL-10: TC-015 (0 — нижче межі) і TC-030 (100 — вище межі) у Run 1 впали з дефектами BUG-003 і BUG-002.

### 4.4. Результат першого прогону (спринт 3, Run 1)

Прогін 27–28.10.2026, виконавець Бойко Є. (ІТ С3), середовища E1 (Chromium 152) і E2 (Chrome 156), Windows 11 — [`docs/sprint3/execution-log.xlsx`](../sprint3/execution-log.xlsx).

| passed | failed | blocked | skipped | Pass rate |
| --- | --- | --- | --- | --- |
| **19** | **12** | 0 | 0 | 19 / 31 · 100 % = **61,3 %** |

Кожен failed-кейс має звіт про дефект: TC-002 (BUG-008), TC-004 (BUG-007), TC-006 (BUG-010), TC-010 (BUG-001), TC-015 (BUG-003), TC-019 (BUG-011, BUG-012), TC-021 (BUG-013), TC-022 (BUG-005), TC-025 (BUG-004), TC-026 (BUG-009, BUG-014), TC-030 (BUG-002), TC-031 (BUG-006). Перевірки цілісності журналу (failed без Bug ID, blocked без пояснення, рядок без дати чи виконавця) — 0.

### 4.5. Ручні кейси на v2 (регресія, спринт 5)

`<Заповнити з regression-log.xlsx після S5-07 і S5-08: дата прогону, виконавці (блок A — TC-001…016, блок B — TC-017…031), passed / failed / blocked, переходи v1 → v2 (стабільно / виправлено / не виправлено / регресія), кейси, що змінили результат.>`

---

## 5. Дефекти

### 5.1. Зведена таблиця трекера

Трекер — [GitHub Issues](https://github.com/ybelinska0/TZYAPZ_TeamB/issues): 14 дефектів demo-застосунку (спринт 3, мітка `sprint:3`) і 6 дефектів модуля бізнес-логіки v1 (спринт 4, мітка `module`). Звіти — за шаблоном спринта 3 (ID, заголовок, severity, priority, опис, кроки, фактичний / очікуваний результат, середовище, вкладення, рецензент).

Колонка «Вимога» — найконкретніша вимога (одна на дефект) за прив'язкою S5-14 `<звірити з traceability-matrix.xlsx після S5-14>`.

| ID | Issue | Джерело | Вимога | Severity | Priority | Статус до регресії | Статус після регресії (v2) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| BUG-001 | [#11](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/11) | demo, TC-010 | F-60 | Major | P3 | New | `<…>` |
| BUG-002 | [#12](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/12) | demo, TC-030 | GUI-30 | Minor | P3 | New | `<…>` |
| BUG-003 | [#13](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/13) | demo, TC-015 | GUI-30 | Normal | P3 | New | `<…>` |
| BUG-004 | [#14](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/14) | demo, TC-025 | U-30 | Minor | P3 | New | `<…>` |
| BUG-005 | [#15](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/15) | demo, TC-022 | F-140 | Normal | P3 | New | `<…>` |
| BUG-006 | [#16](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/16) | demo, TC-031 | BL-20 | Major | P3 | New | `<…>` |
| BUG-007 | [#17](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/17) | demo, TC-004 | GUI-10 | Trivial | P3 | New | `<…>` |
| BUG-008 | [#18](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/18) | demo, TC-002 | U-20 | Minor | P3 | New | `<…>` |
| BUG-009 | [#19](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/19) | demo, TC-026 | F-10 | Normal | P3 | New | `<…>` |
| BUG-010 | [#20](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/20) | demo, TC-006 | F-30 | Normal | P3 | New | `<…>` |
| BUG-011 | [#21](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/21) | demo, TC-019 | F-110 | Major | P3 | New | `<…>` |
| BUG-012 | [#22](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/22) | demo, TC-019 | BL-60 | Normal | P3 | New | `<…>` |
| BUG-013 | [#23](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/23) | demo, TC-021 | F-130 | Minor | P3 | New | `<…>` |
| BUG-014 | [#24](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/24) | demo, TC-026 | GUI-50 | Minor | P3 | New | `<…>` |
| BUG-015 | [#27](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/27) | модуль v1, `DiscountCalculatorTest` | BL-50 | Major | P2 | New → Fixed (передано v2) | `<…>` |
| BUG-016 | [#28](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/28) | модуль v1, `DiscountCalculatorTest` | BL-50 | Minor | P3 | New → Fixed (передано v2) | `<…>` |
| BUG-017 | [#29](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/29) | модуль v1, `CartTest` | BL-10 | Normal | P3 | New → Fixed (передано v2) | `<…>` |
| BUG-018 | [#30](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/30) | модуль v1, `CartTest`, `CartNormalCasesTest` | BL-20 | Major | P2 | New → Fixed (передано v2) | `<…>` |
| BUG-019 | [#50](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/50) | модуль v1, `PromoCodeValidatorRulesTest` | BL-70 | Normal | P3 | New → Fixed (передано v2) | `<…>` |
| BUG-020 | [#38](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/38) | модуль v1, `PromoCodeValidatorRulesTest`, `PromoCodeValidatorReasonsTest` | BL-70 | Major | P2 | New → Fixed (передано v2) | `<…>` |
| `<BUG-021…>` | `<…>` | регресія v2 (S5-13) | `<…>` | `<…>` | `<…>` | — | New |

Статуси після регресії: модуль — `Verified → Closed` або `Reopened` з фактичним результатом на v2 (S5-11, затвердження КТ — S5-12); demo — `Open` («підтверджено на v2, дата») або `Closed (not reproducible)` з причиною. Новий регресійний дефект — окремий звіт `defects-regression.md` із «регресія v2» в заголовку (S5-13).

### 5.2. Розподіл до регресії

| Severity | demo (С3) | модуль (С4) | Разом |
| --- | --- | --- | --- |
| Blocker | 0 | 0 | 0 |
| Critical | 0 | 0 | 0 |
| Major | 3 | 3 | 6 |
| Normal | 5 | 2 | 7 |
| Minor | 5 | 1 | 6 |
| Trivial | 1 | 0 | 1 |
| **Разом** | **14** | **6** | **20** |

За priority: P1 — 0, P2 — 3 (BUG-015, 018, 020), P3 — 17. Дефектів, що блокують виконання кейсів (P1), і дефектів Blocker / Critical не зареєстровано.

Розподіл **відкритих** дефектів після регресії (для критерію К1 і розділу 8): `<після S5-11, S5-12>`.

### 5.3. Приклад повного звіту — BUG-020

[Issue #38](https://github.com/ybelinska0/TZYAPZ_TeamB/issues/38) · модуль v1 · вимоги BL-70, BL-80 · Severity **Major**, Priority **P2** · рецензент Бойко Є. (14.11.2026)

- **Опис.** `PromoCodeValidator` ігнорує ознаку використання одноразового промокоду: `validate` визнає вже використаний одноразовий код придатним, а `apply` повертає для нього знижку замість `IllegalArgumentException`. За BL-70 код придатний лише тоді, коли одноразовий код ще не використаний; за BL-80 `apply` перевіряє придатність і для непридатного коду кидає виняток із причиною відмови.
- **Кроки.** Промокод `WINTER26`, `PERCENT 10`, строк дії 31.12.2026, `singleUse = true`, `used = true`; `today` = 30.12.2026, `subtotal` = 500.00. 1) `validate(...)`; 2) `apply(...)`; 3) те саме для значення 51 %.
- **Фактично.** 1) `valid = true`, причина порожня; 2) повернуто знижку 50.00, винятку немає; 3) причина — про межі відсотка, а не «Код уже використано».
- **Очікувано.** 1) `valid = false`, причина «Код уже використано»; 2) `IllegalArgumentException` із цією причиною; 3) причина «Код уже використано» (вища за пріоритетом за BL-70). Крок 3 спирається на пропоноване уточнення BL-70, що чекає підтвердження замовника; кроки 1–2 — дефект незалежно від нього.
- **Обґрунтування.** Major — один одноразовий код можна застосовувати необмежено, магазин втрачає гроші; P2 — зачіпає кожне застосування одноразового коду.
- **Простежуваність.** Тести: `PromoCodeValidatorRulesTest.singleUseCodeMustNotBeUsed[2]`, `PromoCodeValidatorRulesTest.applyRejectsUsedSingleUseCode`, `PromoCodeValidatorReasonsTest.usedCodeReasonHasPriorityOverInvalidValue` (усі червоні на v1, у коді — коментар `BUG-020`). Середовище: модуль v1, JDK 17, Maven, `mvn test -Dtest=PromoCodeValidatorRulesTest,PromoCodeValidatorReasonsTest`.
- **Результат на v2.** `<з S5-11: результат трьох тестів на v2, статус Verified → Closed або Reopened, дата, виконавець>`
