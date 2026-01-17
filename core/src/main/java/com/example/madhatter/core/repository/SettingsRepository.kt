package com.example.madhatter.core.repository

interface SettingsRepository {
    fun getDefaultCurrencyCode(): String

    fun setDefaultCurrencyCode(currencyCode: String)
}
