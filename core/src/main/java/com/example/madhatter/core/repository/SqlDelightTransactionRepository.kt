package com.example.madhatter.core.repository

import com.example.madhatter.core.Transaction
import com.example.madhatter.core.TransactionType
import com.example.madhatter.core.database.MadHatterDatabase
import com.example.madhatter.core.database.Transactions as DbTransaction
import java.math.BigDecimal
import java.time.Instant

class SqlDelightTransactionRepository(
    private val database: MadHatterDatabase,
) : TransactionRepository {
    override fun getById(id: Long): StoredTransaction? {
        return database.transactionQueries.selectTransactionById(id)
            .executeAsOneOrNull()
            ?.toStoredTransaction()
    }

    override fun getAll(): List<StoredTransaction> {
        return database.transactionQueries.selectAllTransactions()
            .executeAsList()
            .map { it.toStoredTransaction() }
    }

    override fun getByCategory(categoryId: Long): List<StoredTransaction> {
        return database.transactionQueries.selectTransactionsByCategory(categoryId)
            .executeAsList()
            .map { it.toStoredTransaction() }
    }

    override fun insert(transaction: Transaction) {
        database.transactionQueries.insertTransaction(
            timestamp = transaction.timestamp.toString(),
            amount = transaction.amount.toPlainString(),
            currency_code = transaction.currencyCode,
            type = transaction.type.name,
            category_id = transaction.categoryId,
            memo = transaction.memo,
        )
    }

    override fun deleteById(id: Long) {
        database.transactionQueries.deleteTransactionById(id)
    }
}

private fun DbTransaction.toStoredTransaction(): StoredTransaction {
    return StoredTransaction(
        id = id,
        transaction = Transaction(
            timestamp = Instant.parse(timestamp),
            amount = BigDecimal(amount),
            currencyCode = currency_code,
            type = TransactionType.valueOf(type),
            categoryId = category_id,
            memo = memo,
        ),
    )
}
