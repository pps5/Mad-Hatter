package com.example.madhatter.core.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.madhatter.core.database.MadHatterDatabase

object MetroDi {
    private var database: MadHatterDatabase? = null

    fun initialize(context: Context) {
        check(database == null) { "MetroDi is already initialized." }
        val driver = AndroidSqliteDriver(
            schema = MadHatterDatabase.Schema,
            context = context,
            name = "madhatter.db"
        )
        database = MadHatterDatabase(driver)
    }

    fun database(): MadHatterDatabase {
        return requireNotNull(database) { "MetroDi is not initialized." }
    }
}
