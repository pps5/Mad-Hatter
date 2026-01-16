package com.example.madhatter.core.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.madhatter.core.database.MadHatterDatabase
import com.example.madhatter.core.repository.CategoryRepository
import com.example.madhatter.core.repository.SettingsRepository
import com.example.madhatter.core.repository.SharedPreferencesSettingsRepository
import com.example.madhatter.core.repository.SqlDelightCategoryRepository
import com.example.madhatter.core.repository.SqlDelightTransactionRepository
import com.example.madhatter.core.repository.TransactionRepository

object MetroDi {
    private var database: MadHatterDatabase? = null
    private var categoryRepository: CategoryRepository? = null
    private var transactionRepository: TransactionRepository? = null
    private var settingsRepository: SettingsRepository? = null

    fun initialize(context: Context) {
        check(database == null) { "MetroDi is already initialized." }
        val driver = AndroidSqliteDriver(
            schema = MadHatterDatabase.Schema,
            context = context,
            name = "madhatter.db"
        )
        val db = MadHatterDatabase(driver)
        database = db
        categoryRepository = SqlDelightCategoryRepository(db)
        transactionRepository = SqlDelightTransactionRepository(db)
        settingsRepository = SharedPreferencesSettingsRepository(
            context.getSharedPreferences("madhatter_settings", Context.MODE_PRIVATE),
        )
    }

    fun database(): MadHatterDatabase {
        return requireNotNull(database) { "MetroDi is not initialized." }
    }

    fun categoryRepository(): CategoryRepository {
        return requireNotNull(categoryRepository) { "MetroDi is not initialized." }
    }

    fun transactionRepository(): TransactionRepository {
        return requireNotNull(transactionRepository) { "MetroDi is not initialized." }
    }

    fun settingsRepository(): SettingsRepository {
        return requireNotNull(settingsRepository) { "MetroDi is not initialized." }
    }
}
