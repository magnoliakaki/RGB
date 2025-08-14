package com.bloomtregua.rgb.di

import com.bloomtregua.rgb.database.budget.BudgetDao
import com.bloomtregua.rgb.database.budget.BudgetEntity
import com.bloomtregua.rgb.database.categories.CategoryDao
import com.bloomtregua.rgb.database.categories.CategoryEntity
import com.bloomtregua.rgb.database.transactions.TransactionDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

interface CategoryRepository {
    fun getCategorySolaUscita(): Flow<List<CategoryEntity>>
    fun getCategorySolaUscitaByAccount(accountId: Long): Flow<List<CategoryEntity>>
    suspend fun getBudgetSettings(): BudgetEntity?
    suspend fun getLastTransactionDateByCategoryId(categoryId: Long): LocalDate?
    suspend fun getMinTransactionDateByCategoryId(categoryId: Long): LocalDate?
    suspend fun getSumTransactionsFromDateByCategoryId(categoryId: Long, startDate: LocalDate): Double?
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

    override suspend fun getMinTransactionDateByCategoryId(categoryId: Long): LocalDate? {
        return transactionDao.getMinTransactionDateByCategoryId(categoryId)
    }

    override suspend fun getSumTransactionsFromDateByCategoryId(categoryId: Long, startDate: LocalDate): Double? {
        return transactionDao.getSumTransactionsFromDateByCategoryId(categoryId, startDate)
    }
}

