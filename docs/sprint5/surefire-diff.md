# Порівняння Surefire v1 та v2 по кожному тесту (S5-06)

- **Дата:** 24.11.2026
- **Виконавець:** Матвієнко В. (ІТ)
- **Порівнювані звіти:** `docs/sprint5/surefire-v1` vs `docs/sprint5/surefire-v2`

## Зведення та звірка з журналом регресії
- **Загальна кількість запусків Surefire:** 96 у v1 $\rightarrow$ 96 у v2 (41 регресійний тест-метод + 2 запуски `ExampleTest`).
- **ExampleTest:** 2/2 passed на v1 та v2 (стартовий шаблонний тест; виключено з 80 регресійних кейсів згідно з планом регресії).
- **Рядки зі значенням «—» (тест лише в одній версії):** відсутні (0) — усі 96 запусків присутні в обох версіях.
- **Зміни статусів:**
  - `passed → passed` (стабільно): **84**
  - `failed → passed` (виправлено, кандидати на `Verified`): **9**
  - `failed → failed` (не виправлено, кандидати на `Reopened`): **2** (`DiscountCalculatorTest.volumeDiscount_roundsHalfUp`, `PromoCodeValidatorRulesTest.expiryDateIsInclusive[2]`)
  - `passed → failed` (**регресія v2**): **1** (`DiscountCalculatorTest.shippingFee_boundaries[2]`)

## Детальна таблиця переходів

| Тест | v1 | v2 | Перехід |
|---|---|---|---|
| `CartNormalCasesTest#addItemWithQtyInsideRangeIsAccepted(int)[1]` | passed | passed | стабільно |
| `CartNormalCasesTest#addItemWithQtyInsideRangeIsAccepted(int)[2]` | passed | passed | стабільно |
| `CartNormalCasesTest#addItemWithQtyInsideRangeIsAccepted(int)[3]` | passed | passed | стабільно |
| `CartNormalCasesTest#addItemWithQtyInsideRangeIsAccepted(int)[4]` | passed | passed | стабільно |
| `CartNormalCasesTest#changeQtyToBoundaryValueIsAccepted(int)[1]` | passed | passed | стабільно |
| `CartNormalCasesTest#changeQtyToBoundaryValueIsAccepted(int)[2]` | passed | passed | стабільно |
| `CartNormalCasesTest#emptyCartHasZeroSubtotalAndCount` | passed | passed | стабільно |
| `CartNormalCasesTest#reAddingSameSkuIncreasesExistingLine` | failed | passed | виправлено (кандидат на Verified) |
| `CartNormalCasesTest#reAddingUpToMaxQtyIsAccepted` | failed | passed | виправлено (кандидат на Verified) |
| `CartNormalCasesTest#removeItemDeletesOnlyThatLine` | passed | passed | стабільно |
| `CartNormalCasesTest#subtotalAndItemCountOverSeveralLines` | passed | passed | стабільно |
| `CartNormalCasesTest#unitPriceIsRoundedToKopecksHalfUp(String, String)[1]` | passed | passed | стабільно |
| `CartNormalCasesTest#unitPriceIsRoundedToKopecksHalfUp(String, String)[2]` | passed | passed | стабільно |
| `CartNormalCasesTest#unitPriceIsRoundedToKopecksHalfUp(String, String)[3]` | passed | passed | стабільно |
| `CartTest#addItemWithBlankSkuIsRejected(String)[1]` | passed | passed | стабільно |
| `CartTest#addItemWithBlankSkuIsRejected(String)[2]` | passed | passed | стабільно |
| `CartTest#addItemWithBlankSkuIsRejected(String)[3]` | passed | passed | стабільно |
| `CartTest#addItemWithBlankSkuIsRejected(String)[4]` | passed | passed | стабільно |
| `CartTest#addItemWithNonPositivePriceIsRejected(String)[1]` | passed | passed | стабільно |
| `CartTest#addItemWithNonPositivePriceIsRejected(String)[2]` | passed | passed | стабільно |
| `CartTest#addItemWithNonPositivePriceIsRejected(String)[3]` | passed | passed | стабільно |
| `CartTest#addItemWithNonPositivePriceIsRejected(String)[4]` | passed | passed | стабільно |
| `CartTest#addItemWithNonPositivePriceIsRejected(String)[5]` | passed | passed | стабільно |
| `CartTest#addItemWithQtyOutOfRangeIsRejected(int)[1]` | passed | passed | стабільно |
| `CartTest#addItemWithQtyOutOfRangeIsRejected(int)[2]` | passed | passed | стабільно |
| `CartTest#addItemWithQtyOutOfRangeIsRejected(int)[3]` | passed | passed | стабільно |
| `CartTest#addItemWithQtyOutOfRangeIsRejected(int)[4]` | failed | passed | виправлено (кандидат на Verified) |
| `CartTest#addItemWithQtyOutOfRangeIsRejected(int)[5]` | passed | passed | стабільно |
| `CartTest#changeQtyOfMissingSkuThrows` | passed | passed | стабільно |
| `CartTest#changeQtyOutOfRangeIsRejected(int)[1]` | passed | passed | стабільно |
| `CartTest#changeQtyOutOfRangeIsRejected(int)[2]` | passed | passed | стабільно |
| `CartTest#changeQtyOutOfRangeIsRejected(int)[3]` | failed | passed | виправлено (кандидат на Verified) |
| `CartTest#reAddingSameSkuAboveMaxQtyIsRejected` | failed | passed | виправлено (кандидат на Verified) |
| `CartTest#removeMissingSkuThrows` | passed | passed | стабільно |
| `DiscountCalculatorTest#negativeSubtotal_throwsException` | passed | passed | стабільно |
| `DiscountCalculatorTest#negativeSubtotal_volumeDiscountThrowsException` | passed | passed | стабільно |
| `DiscountCalculatorTest#negativeSubtotalAfterDiscount_shippingFeeThrowsException` | passed | passed | стабільно |
| `DiscountCalculatorTest#shippingFee_boundaries(String, String)[1]` | passed | passed | стабільно |
| `DiscountCalculatorTest#shippingFee_boundaries(String, String)[2]` | passed | failed | **регресія (новий дефект)** |
| `DiscountCalculatorTest#shippingFee_boundaries(String, String)[3]` | passed | passed | стабільно |
| `DiscountCalculatorTest#volumeDiscount_normalCases(String, String)[1]` | passed | passed | стабільно |
| `DiscountCalculatorTest#volumeDiscount_normalCases(String, String)[2]` | passed | passed | стабільно |
| `DiscountCalculatorTest#volumeDiscount_normalCases(String, String)[3]` | passed | passed | стабільно |
| `DiscountCalculatorTest#volumeDiscount_roundsHalfUp` | failed | failed | не виправлено (кандидат на Reopened) |
| `DiscountCalculatorTest#volumeDiscountPercent_boundaries(String, int)[1]` | passed | passed | стабільно |
| `DiscountCalculatorTest#volumeDiscountPercent_boundaries(String, int)[2]` | passed | passed | стабільно |
| `DiscountCalculatorTest#volumeDiscountPercent_boundaries(String, int)[3]` | passed | passed | стабільно |
| `DiscountCalculatorTest#volumeDiscountPercent_boundaries(String, int)[4]` | failed | passed | виправлено (кандидат на Verified) |
| `DiscountCalculatorTest#volumeDiscountPercent_boundaries(String, int)[5]` | passed | passed | стабільно |
| `DiscountCalculatorTest#volumeDiscountPercent_boundaries(String, int)[6]` | passed | passed | стабільно |
| `ExampleTest#fivePercentTierAndFreeShipping` | passed | passed | стабільно |
| `ExampleTest#subtotalAndCountOfTwoDifferentItems` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#applyRejectsFixedValueAboveSubtotal` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#applyReturnsDiscountForValidCode(Type, String, String, String)[1]` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#applyReturnsDiscountForValidCode(Type, String, String, String)[2]` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#applyReturnsDiscountForValidCode(Type, String, String, String)[3]` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#applyReturnsDiscountForValidCode(Type, String, String, String)[4]` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#expiredReasonHasPriorityOverUsedCode` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#invalidCodeHasNonEmptyReason(Type, String, boolean)[1]` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#invalidCodeHasNonEmptyReason(Type, String, boolean)[2]` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#invalidCodeHasNonEmptyReason(Type, String, boolean)[3]` | passed | passed | стабільно |
| `PromoCodeValidatorReasonsTest#usedCodeReasonHasPriorityOverInvalidValue` | failed | passed | виправлено (кандидат на Verified) |
| `PromoCodeValidatorRulesTest#applyRejectsExpiredCode` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#applyRejectsUsedSingleUseCode` | failed | passed | виправлено (кандидат на Verified) |
| `PromoCodeValidatorRulesTest#expiredReasonHasPriorityOverInvalidDiscountValue` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#expiryDateIsInclusive(int, boolean)[1]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#expiryDateIsInclusive(int, boolean)[2]` | failed | failed | не виправлено (кандидат на Reopened) |
| `PromoCodeValidatorRulesTest#expiryDateIsInclusive(int, boolean)[3]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#fixedDiscountEqualsCodeValue` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#fixedValueBoundaries(String, boolean)[1]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#fixedValueBoundaries(String, boolean)[2]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#fixedValueBoundaries(String, boolean)[3]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#fixedValueBoundaries(String, boolean)[4]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#percentDiscountRoundsHalfUp` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#percentValueBoundaries(String, boolean)[1]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#percentValueBoundaries(String, boolean)[2]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#percentValueBoundaries(String, boolean)[3]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#percentValueBoundaries(String, boolean)[4]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#singleUseCodeMustNotBeUsed(boolean, boolean, boolean)[1]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#singleUseCodeMustNotBeUsed(boolean, boolean, boolean)[2]` | failed | passed | виправлено (кандидат на Verified) |
| `PromoCodeValidatorRulesTest#singleUseCodeMustNotBeUsed(boolean, boolean, boolean)[3]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#singleUseCodeMustNotBeUsed(boolean, boolean, boolean)[4]` | passed | passed | стабільно |
| `PromoCodeValidatorRulesTest#validCodeIsAccepted` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeLengthBoundaries(String, int, boolean)[1]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeLengthBoundaries(String, int, boolean)[2]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeLengthBoundaries(String, int, boolean)[3]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeLengthBoundaries(String, int, boolean)[4]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeWithAllowedCharactersIsAccepted(String)[1]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeWithAllowedCharactersIsAccepted(String)[2]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeWithAllowedCharactersIsAccepted(String)[3]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeWithInvalidFormatIsRejected(String)[1]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeWithInvalidFormatIsRejected(String)[2]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeWithInvalidFormatIsRejected(String)[3]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeWithInvalidFormatIsRejected(String)[4]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#codeWithInvalidFormatIsRejected(String)[5]` | passed | passed | стабільно |
| `PromoCodeValidatorTest#invalidFormatReasonHasPriorityOverOtherViolations` | passed | passed | стабільно |
