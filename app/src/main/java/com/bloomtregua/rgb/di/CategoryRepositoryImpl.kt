package com.bloomtregua.rgb.di

import com.bloomtregua.rgb.database.budget.BudgetDao
import com.bloomtregua.rgb.database.budget.BudgetEntity
import com.bloomtregua.rgb.database.categories.CategoryDao
import com.bloomtregua.rgb.database.categories.CategoryEntity
import com.bloomtregua.rgb.database.transactions.TransactionDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

interface CategoryRepository {
    fun getCategorySolaUscita(): Flow<List<CategoryEntity>>
    fun getCategorySolaUscitaByAccount(accountId: Long): Flow<List<CategoryEntity>>
    suspend fun getBudgetSettings(): BudgetEntity?
    suspend fun getLastTransactionDateByCategoryId(categoryId: Long): LocalDate?
    suspend fun getLastTransactionDateBySubCategoryId(subCategoryId: Long): LocalDate?
    suspend fun getLastTransactionTimestampByCategoryId(categoryId: Long): LocalDateTime?
    suspend fun getLastTransactionTimestampBySubCategoryId(subCategoryId: Long): LocalDateTime?
    suspend fun getMinTransactionDateByCategoryId(categoryId: Long): LocalDate?
    suspend fun getSumTransactionsFromDateByCategoryId(categoryId: Long, startDate: LocalDate): Double?
    suspend fun getMinTransactionTimestampByCategoryId(categoryId: Long): LocalDateTime?
    suspend fun getSumTransactionsFromTimestampByCategoryId(categoryId: Long, startDate: LocalDateTime): Double?
}

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,     // Hilt inietterà questo da DatabaseModule
    private val budgetDao: BudgetDao,         // Hilt inietterà questo da DatabaseModule
    private val transactionDao: TransactionDao  // Hilt inietterà questo da DatabaseModule
) : CategoryRepository {

    override fun getCategorySolaUscita(): Flow<List<CategoryEntity>> {
        return categoryDao.getCategorySolaUscita()
    }

    override fun getCategorySolaUscitaByAccount(accountId: Long): Flow<List<CategoryEntity>> {
        return categoryDao.getCategorySolaUscitaByAccount(accountId)
    }

    override suspend fun getBudgetSettings(): BudgetEntity? {
        return budgetDao.getBudgetSettings()
    }

    override suspend fun getLastTransactionDateByCategoryId(categoryId: Long): LocalDate? {
        return transactionDao.getLastTransactionDateByCategoryId(categoryId)
    }

    override suspend fun getLastTransactionDateBySubCategoryId(subCategoryId: Long): LocalDate? {
        return transactionDao.getLastTransactionDateBySubCategoryId(subCategoryId)
    }

    override suspend fun getLastTransactionTimestampByCategoryId(categoryId: Long): LocalDateTime? {
        return transactionDao.getLastTransactionTimestampByCategoryId(categoryId)
    }

    override suspend fun getLastTransactionTimestampBySubCategoryId(subCategoryId: Long): LocalDateTime? {
        return transactionDao.getLastTransactionTimestampBySubCategoryId(subCategoryId)
    }

    override suspend fun getMinTransactionDateByCategoryId(categoryId: Long): LocalDate? {
        return transactionDao.getMinTransactionDateByCategoryId(categoryId)
    }

    override suspend fun getMinTransactionTimestampByCategoryId(categoryId: Long): LocalDateTime? {
        return transactionDao.getMinTransactionTimestampByCategoryId(categoryId)
    }

    override suspend fun getSumTransactionsFromDateByCategoryId(categoryId: Long, startDate: LocalDate): Double? {
        return transactionDao.getSumTransactionsFromDateByCategoryId(categoryId, startDate)
    }

    override suspend fun getSumTransactionsFromTimestampByCategoryId(categoryId: Long, startDate: LocalDateTime): Double? {
        return transactionDao.getSumTransactionsFromTimestampByCategoryId(categoryId, startDate)
    }
}

