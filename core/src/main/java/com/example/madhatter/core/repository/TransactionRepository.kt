package com.example.madhatter.core.repository

import com.example.madhatter.core.Transaction

interface TransactionRepository {
    fun getById(id: Long): StoredTransaction?

    fun getAll(): List<StoredTransaction>

    fun getByCategory(categoryId: Long): List<StoredTransaction>

    fun insert(transaction: Transaction)

    fun deleteById(id: Long)
}

data class StoredTransaction(
    val id: Long,
    val transaction: Transaction,
)
