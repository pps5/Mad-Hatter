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
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.JAPAN)
        val latestTimestamp = latestTransaction?.transaction?.timestamp?.formatWith(formatter) ?: "-"
        val items = buildList {
            add(
                HomeDashboardItem(
                    title = "アクティブカテゴリ",
                    description = "${categories.size}件のカテゴリが利用可能です。",
                    status = "カテゴリ",
                    timestamp = "現在",
                ),
            )
            add(
                HomeDashboardItem(
                    title = "登録済み取引",
                    description = "全${transactions.size}件の取引があります。",
                    status = "取引",
                    timestamp = latestTimestamp,
                ),
            )
            latestTransaction?.let { stored ->
                val transaction = stored.transaction
                add(
                    HomeDashboardItem(
                        title = "最新の取引",
                        description = buildString {
                            append(formatAmount(transaction.amount))
                            append(" ")
                            append(transaction.currencyCode)
                            append(" ・ ")
                            append(transaction.memo.ifBlank { "メモなし" })
                        },
                        status = transaction.type.toLabel(),
                        timestamp = transaction.timestamp.formatWith(formatter),
                    ),
                )
            }
            if (categories.isNotEmpty()) {
                add(
                    HomeDashboardItem(
                        title = "最近のカテゴリ",
                        description = categories
                            .take(3)
                            .joinToString(separator = "、") { it.name },
                        status = "カテゴリ",
                        timestamp = "-",
                    ),
                )
            }
        }
        uiState = uiState.copy(
            items = items,
            latestTransactionId = latestTransaction?.id,
        )
    }
}

data class HomeUiState(
    val items: List<HomeDashboardItem> = emptyList(),
    val latestTransactionId: Long? = null,
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
