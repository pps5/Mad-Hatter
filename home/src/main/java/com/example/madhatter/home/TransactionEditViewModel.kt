package com.example.madhatter.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.madhatter.core.TransactionType
import java.util.Locale

class TransactionEditViewModel : ViewModel() {
    var state by mutableStateOf(defaultState())
        private set

    fun onTimestampChange(value: String) {
        state = state.copy(timestamp = value)
    }

    fun onAmountChange(value: String) {
        state = state.copy(amount = value)
    }

    fun onCurrencyCodeChange(value: String) {
        state = state.copy(currencyCode = value.uppercase(Locale.ROOT))
    }

    fun onTypeChange(value: TransactionType) {
        state = state.copy(type = value)
    }

    fun onCategoryChange(value: Long) {
        state = state.copy(selectedCategoryId = value)
    }

    fun onMemoChange(value: String) {
        state = state.copy(memo = value)
    }

    fun onSave() {}

    fun onDelete() {}

    fun onCancel() {}

    private fun defaultState(): TransactionEditUiState {
        return TransactionEditUiState(
            timestamp = "2024-04-08 10:30",
            amount = "1280.00",
            currencyCode = "JPY",
            type = TransactionType.EXPENSE,
            categories = listOf(
                TransactionCategoryOption(
                    id = 1L,
                    name = "カフェ",
                    type = TransactionType.EXPENSE,
                    parentName = "飲食",
                ),
                TransactionCategoryOption(
                    id = 2L,
                    name = "交通費",
                    type = TransactionType.EXPENSE,
                ),
                TransactionCategoryOption(
                    id = 3L,
                    name = "ギフト",
                    type = TransactionType.INCOME,
                ),
            ),
            selectedCategoryId = 1L,
            memo = "帽子職人との打ち合わせ代",
            isEditing = true,
        )
    }
}
