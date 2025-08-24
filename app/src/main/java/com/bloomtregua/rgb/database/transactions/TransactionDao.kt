package com.bloomtregua.rgb.database.transactions

import androidx.room.Insert
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transactionId = :transactionId")
    fun getTransactionById(transactionId: Long): Flow<TransactionEntity>

    @Query("""
       SELECT MAX(transactionDate)
       FROM transactions
       WHERE transactionCategoryId = :categoryId
   """)
    suspend fun getLastTransactionDateByCategoryId(categoryId: Long): LocalDate?

    @Query("""
       SELECT MAX(transactionDate)
       FROM transactions
       WHERE transactionSubCategoryId = :subCategoryId
   """)
    suspend fun getLastTransactionDateBySubCategoryId(subCategoryId: Long): LocalDate?

    @Query("""
       SELECT MAX(transactionTimestamp)
       FROM transactions
       WHERE transactionCategoryId = :categoryId
   """)
    suspend fun getLastTransactionTimestampByCategoryId(categoryId: Long): LocalDateTime?

    @Query("""
       SELECT MAX(transactionTimestamp)
       FROM transactions
       WHERE transactionSubCategoryId = :subCategoryId
   """)
    suspend fun getLastTransactionTimestampBySubCategoryId(subCategoryId: Long): LocalDateTime?

    @Query("""
       SELECT MIN(transactionDate)
       FROM transactions
       WHERE transactionCategoryId = :categoryId
   """)
    suspend fun getMinTransactionDateByCategoryId(categoryId: Long): LocalDate?

    @Query("""
       SELECT MIN(transactionTimestamp)
       FROM transactions
       WHERE transactionCategoryId = :categoryId
   """)
    suspend fun getMinTransactionTimestampByCategoryId(categoryId: Long): LocalDateTime?

    @Query("""
       SELECT SUM(COALESCE(transactionAmount,0)*COALESCE(transactionSign,-1))
       FROM transactions
       WHERE transactionCategoryId = :categoryId
         AND transactionDate >= :startDate
         AND transactionDate <= date('now')
         AND transactionDaContabilizzare = false
   """)
    suspend fun getSumTransactionsFromDateByCategoryId(
        categoryId: Long,
        startDate: LocalDate
    ): Double?

    @Query("""
       SELECT SUM(COALESCE(transactionAmount,0)*COALESCE(transactionSign,-1))
       FROM transactions
       WHERE transactionCategoryId = :categoryId
         AND transactionTimestamp >= :startDate
         AND transactionTimestamp < date('now', '+1 day')
         AND transactionDaContabilizzare = false
   """)
    suspend fun getSumTransactionsFromTimestampByCategoryId(
        categoryId: Long,
        startDate: LocalDateTime
    ): Double?

    @Query("""
       SELECT SUM(COALESCE(transactionAmount,0)*COALESCE(transactionSign,-1))
       FROM transactions
       WHERE transactionSubCategoryId = :subcategoryId
         AND transactionDate >= :startDate
         AND transactionDate <= date('now')
         AND transactionDaContabilizzare = false
   """)
    suspend fun getSumTransactionsFromDateBySubcategoryId(
        subcategoryId: Long,
        startDate: LocalDate
    ): Double?

    @Query("""
       SELECT SUM(COALESCE(transactionAmount,0)*COALESCE(transactionSign,-1))
       FROM transactions
       WHERE transactionSubCategoryId = :subcategoryId
         AND transactionTimestamp >= :startDate
         AND transactionTimestamp < date('now', '+1 day')
         AND transactionDaContabilizzare = false
   """)
    suspend fun getSumTransactionsFromTimestampBySubcategoryId(
        subcategoryId: Long,
        startDate: LocalDateTime
    ): Double?

    @Query("SELECT c.categoryAccountId FROM transactions t INNER JOIN categories c ON t.transactionCategoryId = c.categoryId WHERE transactionId = :transactionId")
    suspend fun getAccountByTransactionId(transactionId: Long): Long?

    @Query("SELECT * FROM transactions WHERE transactionDaContabilizzare = true AND transactionDate <= :currentDate")
    suspend fun getPendingTransactionsForAccounting(currentDate: LocalDate): List<TransactionEntity>

    @Query("UPDATE transactions SET transactionDaContabilizzare = false WHERE transactionId = :transactionId")
    suspend fun markTransactionAsAccounted(transactionId: Long)
}