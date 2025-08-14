package com.bloomtregua.rgb.database.transactions

import androidx.room.Insert
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

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
    fun getTransactionById(transactionId: Int): Flow<TransactionEntity>

    @Query("""
       SELECT MAX(transactionDate)
       FROM transactions
       WHERE transactionCategoryId = :categoryId
   """)
    suspend fun getLastTransactionDateByCategoryId(categoryId: Long): LocalDate?

    @Query("""
       SELECT MIN(transactionDate)
       FROM transactions
       WHERE transactionCategoryId = :categoryId
   """)
    suspend fun getMinTransactionDateByCategoryId(categoryId: Long): LocalDate?

    @Query("""
       SELECT SUM(COALESCE(transactionAmount,0)*COALESCE(transactionSign,-1))
       FROM transactions
       WHERE transactionCategoryId = :categoryId
         AND transactionDate >= :startDate
         AND transactionDate <= date('now')
   """)
    suspend fun getSumTransactionsFromDateByCategoryId(
        categoryId: Long,
        startDate: LocalDate
    ): Double?
}