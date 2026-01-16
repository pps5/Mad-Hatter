package com.example.madhatter.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.madhatter.core.CurrencyConfig
import com.example.madhatter.core.repository.SettingsRepository

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    var uiState by mutableStateOf(
        SettingsUiState(
            selectedCurrencyCode = settingsRepository.getDefaultCurrencyCode(),
            currencyPresets = CurrencyConfig.presets,
        ),
    )
        private set

    fun onCurrencySelected(currencyCode: String) {
        settingsRepository.setDefaultCurrencyCode(currencyCode)
        uiState = uiState.copy(selectedCurrencyCode = currencyCode)
    }
}

data class SettingsUiState(
    val selectedCurrencyCode: String,
    val currencyPresets: List<String>,
)

class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
