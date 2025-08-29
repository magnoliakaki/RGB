package com.bloomtregua.rgb.di

import com.bloomtregua.rgb.database.transactions.TransactionDao
import com.bloomtregua.rgb.database.transactions.TransactionEntity
import com.bloomtregua.rgb.database.transactions.TransactionWithCategoryName
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    suspend fun getTransactionById(transactionId: Long): Flow<TransactionEntity>
    suspend fun insertTransaction(transaction: TransactionEntity)
    suspend fun updateTransaction(transaction: TransactionEntity)
    suspend fun getPendingTransactionsForAccounting(currentDate: LocalDate): List<TransactionEntity>
    suspend fun markTransactionAsAccounted(transactionId: Long)
    suspend fun getAccountByTransactionId(transactionId: Long): Long?
    fun getFutureTransactionsWithCategoryName(maxRecord: Int, accountId : Long): Flow<List<TransactionWithCategoryName>>
}

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    override suspend fun getTransactionById(transactionId: Long): Flow<TransactionEntity> {
        return transactionDao.getTransactionById(transactionId)
    }

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    override suspend fun getPendingTransactionsForAccounting(currentDate: LocalDate): List<TransactionEntity> {
        return transactionDao.getPendingTransactionsForAccounting(currentDate)
    }

    override suspend fun markTransactionAsAccounted(transactionId: Long) {
        transactionDao.markTransactionAsAccounted(transactionId)
    }

    override suspend fun getAccountByTransactionId(transactionId: Long): Long? {
        return transactionDao.getAccountByTransactionId(transactionId)
    }

    override fun getFutureTransactionsWithCategoryName(maxRecord: Int, accountId : Long): Flow<List<TransactionWithCategoryName>> {
        return transactionDao.getFutureTransactionsWithCategoryName(maxRecord, accountId)
    }

}