package com.example.madhatter.core

import java.math.BigDecimal
import java.time.Instant

/**
 * Domain transaction model.
 *
 * - Amounts are stored as [BigDecimal] with a minimum unit of 0.01.
 * - Currency is mandatory and stored as an ISO-4217 code (e.g., "USD").
 * - Timestamps are stored in UTC as [Instant].
 */
data class Transaction(
    val timestamp: Instant,
    val amount: BigDecimal,
    val currencyCode: String,
    val type: TransactionType,
    val categoryId: Long,
    val memo: String,
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (amount <= BigDecimal.ZERO) {
            errors.add("amount must be greater than 0")
        }

        if (amount.remainder(MIN_UNIT).compareTo(BigDecimal.ZERO) != 0) {
            errors.add("amount must be in increments of $MIN_UNIT")
        }

        if (currencyCode.isBlank()) {
            errors.add("currencyCode is required")
        } else if (!CURRENCY_CODE_REGEX.matches(currencyCode)) {
            errors.add("currencyCode must be ISO-4217 format (e.g., USD)")
        }

        if (categoryId <= 0L) {
            errors.add("categoryId must be greater than 0")
        }

        if (memo.length > MEMO_MAX_LENGTH) {
            errors.add("memo must be $MEMO_MAX_LENGTH characters or less")
        }

        return errors
    }

    companion object {
        private const val MEMO_MAX_LENGTH = 200
        private val MIN_UNIT = BigDecimal("0.01")
        private val CURRENCY_CODE_REGEX = Regex("^[A-Z]{3}$")
    }
}

enum class TransactionType {
    INCOME,
    EXPENSE,
}
