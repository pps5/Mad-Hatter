package com.example.madhatter.core.repository

import android.content.SharedPreferences
import com.example.madhatter.core.CurrencyConfig

class SharedPreferencesSettingsRepository(
    private val sharedPreferences: SharedPreferences,
) : SettingsRepository {
    override fun getDefaultCurrencyCode(): String {
        return sharedPreferences
            .getString(KEY_DEFAULT_CURRENCY_CODE, CurrencyConfig.DEFAULT_CURRENCY_CODE)
            ?.uppercase()
            ?: CurrencyConfig.DEFAULT_CURRENCY_CODE
    }

    override fun setDefaultCurrencyCode(currencyCode: String) {
        sharedPreferences
            .edit()
            .putString(KEY_DEFAULT_CURRENCY_CODE, currencyCode.uppercase())
            .apply()
    }

    companion object {
        private const val KEY_DEFAULT_CURRENCY_CODE = "default_currency_code"
    }
}
