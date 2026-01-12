package com.example.madhatter.core

import java.math.BigDecimal
import java.time.Instant

data class Transaction(
    val timestamp: Instant,
    val amount: BigDecimal,
    val type: TransactionType,
    val categoryId: String,
    val memo: String,
)

enum class TransactionType {
    INCOME,
    EXPENSE,
}
