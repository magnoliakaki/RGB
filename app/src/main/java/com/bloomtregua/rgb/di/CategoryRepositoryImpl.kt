package com.bloomtregua.rgb.di

import com.bloomtregua.rgb.database.budget.BudgetDao
import com.bloomtregua.rgb.database.budget.BudgetEntity
import com.bloomtregua.rgb.database.categories.CategoryDao
import com.bloomtregua.rgb.database.categories.CategoryEntity
import com.bloomtregua.rgb.database.categories.MacroCategoryDao
import com.bloomtregua.rgb.database.categories.MacroCategoryEntity
import com.bloomtregua.rgb.database.categories.SubcategoryDao
import com.bloomtregua.rgb.database.categories.SubcategoryEntity
import com.bloomtregua.rgb.database.transactions.TransactionDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

interface CategoryRepository {
    fun getCategoryById(categoryId: Long): Flow<CategoryEntity>
    fun getSubcategoryByCategoryId(subcategoryCategoryId: Long): Flow<List<SubcategoryEntity>>
    fun getSubcategoryById(subcategoryId: Long): Flow<SubcategoryEntity>
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
    suspend fun getSumTransactionsFromDateBySubcategoryId(subcategoryId: Long, startDate: LocalDate): Double?
    suspend fun getSumTransactionsFromTimestampBySubcategoryId(subcategoryId: Long, startDate: LocalDateTime): Double?
    suspend fun getPresenzaAllertInAccountOrCategory(accountId: Long, categoryId: Long?): Int
    suspend fun updateCategory(category: CategoryEntity)
    suspend fun updateSubcategory(subcategory: SubcategoryEntity)
    fun getAllMacroCategories(): Flow<List<MacroCategoryEntity>>
    fun getAllCategories(): Flow<List<CategoryEntity>>
    fun getCategoryByIdOnce(categoryId: Long): CategoryEntity?
}

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,     // Hilt inietterà questo da DatabaseModule
    private val subcategoryDao: SubcategoryDao, // Hilt inietterà questo da DatabaseModule
    private val budgetDao: BudgetDao,         // Hilt inietterà questo da DatabaseModule
    private val transactionDao: TransactionDao,  // Hilt inietterà questo da DatabaseModule
    private val macroCategoryDao: MacroCategoryDao
) : CategoryRepository {

    override fun getCategoryById(categoryId: Long): Flow<CategoryEntity> {
        return categoryDao.getCategoryById(categoryId)
    }

    override fun getSubcategoryByCategoryId(subcategoryCategoryId: Long): Flow<List<SubcategoryEntity>> {
        return subcategoryDao.getSubcategoryByCategoryId(subcategoryCategoryId)
    }

    override fun getSubcategoryById(subcategoryId: Long): Flow<SubcategoryEntity> {
        return subcategoryDao.getSubcategoryById(subcategoryId)
    }

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

    override suspend fun getSumTransactionsFromDateBySubcategoryId(subcategoryId: Long, startDate: LocalDate): Double? {
        return transactionDao.getSumTransactionsFromDateBySubcategoryId(subcategoryId, startDate)
    }

    override suspend fun getSumTransactionsFromTimestampBySubcategoryId(subcategoryId: Long, startDate: LocalDateTime): Double? {
        return transactionDao.getSumTransactionsFromTimestampBySubcategoryId(subcategoryId, startDate)
    }

    override suspend fun getPresenzaAllertInAccountOrCategory(accountId: Long, categoryId: Long?): Int {
        return categoryDao.getPresenzaAllertInAccountOrCategory(accountId, categoryId)
    }

    override suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    override suspend fun updateSubcategory(subcategory: SubcategoryEntity) {
        subcategoryDao.updateSubcategory(subcategory)
    }

    override fun getAllMacroCategories(): Flow<List<MacroCategoryEntity>> {
        return macroCategoryDao.getAllMacroCategories()
    }

    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    override fun getCategoryByIdOnce(categoryId: Long): CategoryEntity? {
        return categoryDao.getCategoryByIdOnce(categoryId)
    }
}

