package com.example.madhatter.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.madhatter.core.Category
import com.example.madhatter.core.Transaction
import com.example.madhatter.core.TransactionType
import com.example.madhatter.core.repository.CategoryRepository
import com.example.madhatter.core.repository.TransactionRepository
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class TransactionEditorViewModel(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionId: Long?,
) : ViewModel() {
    var uiState by mutableStateOf(TransactionEditorUiState())
        private set

    init {
        loadInitialState()
    }

    fun onAmountChange(amount: String) {
        uiState = uiState.copy(amountInput = amount)
    }

    fun onCurrencyChange(currency: String) {
        uiState = uiState.copy(currencyInput = currency)
    }

    fun onMemoChange(memo: String) {
        uiState = uiState.copy(memoInput = memo)
    }

    fun onTypeChange(type: TransactionType) {
        uiState = uiState.copy(type = type)
    }

    fun onCategorySelected(categoryId: Long) {
        uiState = uiState.copy(selectedCategoryId = categoryId)
    }

    fun saveTransaction(): Boolean {
        val validationErrors = validateInputs()
        if (validationErrors.isNotEmpty()) {
            uiState = uiState.copy(validationErrors = validationErrors)
            return false
        }

        val amount = uiState.amountInput.trim().toBigDecimalOrNull()
        val categoryId = uiState.selectedCategoryId
        if (amount == null || categoryId == null) {
            return false
        }

        val transaction = Transaction(
            timestamp = uiState.timestamp,
            amount = amount,
            currencyCode = uiState.currencyInput.trim().uppercase(),
            type = uiState.type,
            categoryId = categoryId,
            memo = uiState.memoInput.trim(),
        )

        if (uiState.isEditing && transactionId != null) {
            transactionRepository.deleteById(transactionId)
        }
        transactionRepository.insert(transaction)
        uiState = uiState.copy(validationErrors = emptyList())
        return true
    }

    private fun loadInitialState() {
        val categories = categoryRepository.getActive()
        val storedTransaction = transactionId?.let { transactionRepository.getById(it) }
        val transaction = storedTransaction?.transaction
        val timestamp = transaction?.timestamp ?: Instant.now()
        val selectedCategoryId = transaction?.categoryId ?: categories.firstOrNull()?.id
        uiState = uiState.copy(
            isEditing = transaction != null,
            amountInput = transaction?.amount?.stripTrailingZeros()?.toPlainString().orEmpty(),
            currencyInput = transaction?.currencyCode.orEmpty(),
            memoInput = transaction?.memo.orEmpty(),
            type = transaction?.type ?: TransactionType.EXPENSE,
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            timestamp = timestamp,
            formattedTimestamp = formatTimestamp(timestamp),
        )
    }

    private fun validateInputs(): List<TransactionEditorValidationError> {
        val errors = mutableListOf<TransactionEditorValidationError>()
        val amountText = uiState.amountInput.trim()
        val amount = amountText.toBigDecimalOrNull()
        when {
            amountText.isBlank() -> errors.add(TransactionEditorValidationError.AmountRequired)
            amount == null -> errors.add(TransactionEditorValidationError.AmountInvalid)
            amount <= BigDecimal.ZERO -> errors.add(TransactionEditorValidationError.AmountInvalid)
            amount.remainder(MIN_UNIT).compareTo(BigDecimal.ZERO) != 0 ->
                errors.add(TransactionEditorValidationError.AmountInvalid)
        }

        val currency = uiState.currencyInput.trim()
        if (currency.isBlank()) {
            errors.add(TransactionEditorValidationError.CurrencyRequired)
        } else if (!CURRENCY_REGEX.matches(currency.uppercase())) {
            errors.add(TransactionEditorValidationError.CurrencyInvalid)
        }

        if (uiState.selectedCategoryId == null) {
            errors.add(TransactionEditorValidationError.CategoryRequired)
        }

        if (uiState.memoInput.length > MEMO_MAX_LENGTH) {
            errors.add(TransactionEditorValidationError.MemoTooLong)
        }

        return errors
    }

    private fun formatTimestamp(timestamp: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.JAPAN)
        return formatter.format(timestamp.atZone(ZoneId.systemDefault()))
    }

    companion object {
        private val MIN_UNIT = BigDecimal("0.01")
        private val CURRENCY_REGEX = Regex("^[A-Z]{3}$")
        private const val MEMO_MAX_LENGTH = 200
    }
}

data class TransactionEditorUiState(
    val isEditing: Boolean = false,
    val amountInput: String = "",
    val currencyInput: String = "",
    val memoInput: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val timestamp: Instant = Instant.now(),
    val formattedTimestamp: String = "",
    val validationErrors: List<TransactionEditorValidationError> = emptyList(),
)

enum class TransactionEditorValidationError {
    AmountRequired,
    AmountInvalid,
    CurrencyRequired,
    CurrencyInvalid,
    CategoryRequired,
    MemoTooLong,
}

class TransactionEditorViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionId: Long?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionEditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionEditorViewModel(
                categoryRepository,
                transactionRepository,
                transactionId,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
