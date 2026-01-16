package com.example.madhatter.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.madhatter.core.TransactionType
import com.example.madhatter.core.repository.CategoryRepository
import com.example.madhatter.core.repository.TransactionRepository
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeViewModel(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        val categories = categoryRepository.getActive()
        val transactions = transactionRepository.getAll()
        val latestTransaction = transactions.maxByOrNull { it.transaction.timestamp }
        val latestCategoryId = categories.maxByOrNull { it.id }?.id
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.JAPAN)
        val summary = buildSummary(transactions)
        val latestTransactions = transactions
            .sortedByDescending { it.transaction.timestamp }
            .take(5)
            .map { stored ->
                val transaction = stored.transaction
                LatestTransactionItem(
                    id = stored.id,
                    title = transaction.memo.ifBlank { "メモなし" },
                    amount = formatAmount(transaction.amount),
                    currencyCode = transaction.currencyCode,
                    typeLabel = transaction.type.toLabel(),
                    timestamp = transaction.timestamp.formatWith(formatter),
                )
            }
        uiState = uiState.copy(
            summary = summary,
            latestTransactions = latestTransactions,
            latestTransactionId = latestTransaction?.id,
            latestCategoryId = latestCategoryId,
        )
    }
}

data class HomeUiState(
    val summary: TransactionSummary = TransactionSummary(),
    val latestTransactions: List<LatestTransactionItem> = emptyList(),
    val latestTransactionId: Long? = null,
    val latestCategoryId: Long? = null,
)

class HomeViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(categoryRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

private fun Instant.formatWith(formatter: DateTimeFormatter): String {
    return formatter.format(atZone(ZoneId.systemDefault()))
}

private fun TransactionType.toLabel(): String {
    return when (this) {
        TransactionType.INCOME -> "収入"
        TransactionType.EXPENSE -> "支出"
    }
}

private fun formatAmount(amount: BigDecimal): String {
    return amount.stripTrailingZeros().toPlainString()
}

private fun buildSummary(transactions: List<com.example.madhatter.core.repository.StoredTransaction>): TransactionSummary {
    val incomeTotal = transactions
        .filter { it.transaction.type == TransactionType.INCOME }
        .fold(BigDecimal.ZERO) { acc, stored -> acc + stored.transaction.amount }
    val expenseTotal = transactions
        .filter { it.transaction.type == TransactionType.EXPENSE }
        .fold(BigDecimal.ZERO) { acc, stored -> acc + stored.transaction.amount }
    val currencyCodes = transactions.map { it.transaction.currencyCode }.distinct()
    val currencyLabel = when (currencyCodes.size) {
        0 -> "-"
        1 -> currencyCodes.first()
        else -> "複数通貨"
    }
    return TransactionSummary(
        transactionCount = transactions.size,
        incomeTotal = incomeTotal,
        expenseTotal = expenseTotal,
        netTotal = incomeTotal - expenseTotal,
        currencyLabel = currencyLabel,
    )
}

data class TransactionSummary(
    val transactionCount: Int = 0,
    val incomeTotal: BigDecimal = BigDecimal.ZERO,
    val expenseTotal: BigDecimal = BigDecimal.ZERO,
    val netTotal: BigDecimal = BigDecimal.ZERO,
    val currencyLabel: String = "-",
)

data class LatestTransactionItem(
    val id: Long,
    val title: String,
    val amount: String,
    val currencyCode: String,
    val typeLabel: String,
    val timestamp: String,
)
