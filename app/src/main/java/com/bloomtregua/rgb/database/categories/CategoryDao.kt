package com.bloomtregua.rgb.database.categories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteTCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    fun getCategoryById(categoryId: Long): Flow<CategoryEntity>

    @Query("SELECT * FROM categories where categoryIncome = false")
    fun getCategorySolaUscita(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories where categoryIncome = false and categoryAccountId = :accoundID")
    fun getCategorySolaUscitaByAccount(accoundID: Long): Flow<List<CategoryEntity>>

    @Query("""
    WITH
        Categorie_associate_conto AS (
            SELECT
                c.categoryId,
                c.categoryAllAmount
            FROM
                categories c
            WHERE
                categoryAccountId = :accountId
                AND (categoryId = :categoryId OR :categoryId = NULL)
                AND categoryIncome = false
        ),
        BudgetInfo AS (
            SELECT
                b.budgetId,
                b.budgetResetType,
                b.budgetResetCategory,
                b.budgetResetSubCategory,
                b.budgetResetDay
            FROM
                Budgets b
        ),
        ResetPoint AS (
            SELECT
                bi.budgetId,
                CASE
                    WHEN bi.budgetResetType = 'CATEGORY' THEN (
                        SELECT
                            COALESCE(
                                CASE
                                    WHEN bi.budgetResetSubCategory IS NULL THEN (
                                        SELECT
                                            MAX(t.transactionTimestamp)
                                        FROM
                                            Transactions t
                                        WHERE
                                            t.transactionCategoryId = bi.budgetResetCategory
                                    )
                                    ELSE (
                                        SELECT
                                            MAX(t.transactionTimestamp)
                                        FROM
                                            Transactions t
                                        WHERE
                                            t.transactionSubCategoryId = bi.budgetResetSubCategory
                                    )
                                END,
                                ( -- Fallback se MAX è NULL
                                    SELECT
                                        MIN(t.transactionTimestamp)
                                    FROM
                                        Transactions t
                                    WHERE
                                        (
                                            bi.budgetResetSubCategory IS NULL AND t.transactionCategoryId = bi.budgetResetCategory
                                        )
                                        OR (
                                            bi.budgetResetSubCategory IS NOT NULL AND t.transactionSubCategoryId = bi.budgetResetSubCategory
                                        )
                                )
                            )
                    )
                    WHEN bi.budgetResetType = 'DATE' THEN (
                        CASE
                            WHEN CAST(strftime('%d', 'now', 'localtime') AS INTEGER) >= bi.budgetResetDay THEN
                                date(
                                    strftime('%Y', 'now', 'localtime') || '-' ||
                                    printf('%02d', CAST(strftime('%m', 'now', 'localtime') AS INTEGER)) || '-' ||
                                    printf('%02d', MIN(bi.budgetResetDay, CAST(strftime('%d', date('now', 'localtime', 'start of month', '+1 month', '-1 day')) AS INTEGER)))
                                )
                            ELSE
                                date(
                                    strftime('%Y', date('now', 'localtime', '-1 month')) || '-' ||
                                    printf('%02d', CAST(strftime('%m', date('now', 'localtime', '-1 month')) AS INTEGER)) || '-' ||
                                    printf('%02d', MIN(bi.budgetResetDay, CAST(strftime('%d', date('now', 'localtime', '-1 month', 'start of month', '+1 month', '-1 day')) AS INTEGER)))
                                )
                        END
                    )
                END AS startDate
            FROM
                BudgetInfo bi
        ),
        giorno_transazione AS (
            SELECT
                CASE
                    WHEN bi.budgetResetType = 'CATEGORY' THEN
                        IFNULL(RP.startDate, '2001-01-01')
                    WHEN bi.budgetResetType = 'DATE' THEN
                        CASE strftime('%w', RP.startDate)
                            WHEN '6' THEN date(RP.startDate, '+2 days') -- Sabato -> Lunedì
                            WHEN '0' THEN date(RP.startDate, '+1 day')   -- Domenica -> Lunedì
                            ELSE RP.startDate
                        END
                END AS dataTransazione
            FROM
                BudgetInfo bi
                CROSS JOIN ResetPoint RP
        ),
        CategorySubcategorySums AS (
            SELECT
                t.transactionCategoryId,
                t.transactionSubCategoryId,
                s.subcategoryAllAmount,
                CASE
                    WHEN bi.budgetResetType = 'CATEGORY' THEN (
                        SELECT
                            SUM(COALESCE(t2.transactionAmount, 0) * COALESCE(t2.transactionSign, -1)) * -1
                        FROM
                            Transactions t2
                            CROSS JOIN giorno_transazione gt
                        WHERE
                            t2.transactionCategoryId = t.transactionCategoryId
                            AND t2.transactionSubCategoryId = t.transactionSubCategoryId
                            AND t2.transactionTimestamp >= gt.dataTransazione
                            AND t2.transactionTimestamp < date('now', '+1 day')
                            AND t2.transactionDaContabilizzare = false
                    )
                    WHEN bi.budgetResetType = 'DATE' THEN (
                        SELECT
                            SUM(COALESCE(t2.transactionAmount, 0) * COALESCE(t2.transactionSign, -1)) * -1
                        FROM
                            Transactions t2
                            CROSS JOIN giorno_transazione gt
                        WHERE
                            t2.transactionCategoryId = t.transactionCategoryId
                            AND t2.transactionSubCategoryId = t.transactionSubCategoryId
                            AND t2.transactionDate >= gt.dataTransazione
                            AND t2.transactionDate <= date('now')
                            AND t2.transactionDaContabilizzare = false
                    )
                END AS ImportoSottocategoria
            FROM
                transactions t
                INNER JOIN Categorie_associate_conto cac ON t.transactionCategoryId = cac.categoryId
                INNER JOIN subcategories s ON s.subcategoryId = t.transactionSubCategoryId
                CROSS JOIN Budgets bi
            GROUP BY
                t.transactionCategoryId,
                t.transactionSubCategoryId,
                s.subcategoryAllAmount
        ),
        CategoryTotals AS (
            SELECT
                t.transactionCategoryId,
                cac.categoryAllAmount,
                CASE
                    WHEN bi.budgetResetType = 'CATEGORY' THEN (
                        SELECT
                            SUM(COALESCE(t2.transactionAmount, 0) * COALESCE(t2.transactionSign, -1)) * -1
                        FROM
                            Transactions t2
                            CROSS JOIN giorno_transazione gt
                        WHERE
                            t2.transactionCategoryId = t.transactionCategoryId
                            AND t2.transactionTimestamp >= gt.dataTransazione
                            AND t2.transactionTimestamp < date('now', '+1 day')
                            AND t2.transactionDaContabilizzare = false
                    )
                    WHEN bi.budgetResetType = 'DATE' THEN (
                        SELECT
                            SUM(COALESCE(t2.transactionAmount, 0) * COALESCE(t2.transactionSign, -1)) * -1
                        FROM
                            Transactions t2
                            CROSS JOIN giorno_transazione gt
                        WHERE
                            t2.transactionCategoryId = t.transactionCategoryId
                            AND t2.transactionDate >= gt.dataTransazione
                            AND t2.transactionDate <= date('now')
                            AND t2.transactionDaContabilizzare = false
                    )
                END AS ImportoCategoria
            FROM
                Transactions t
                INNER JOIN Categorie_associate_conto cac ON t.transactionCategoryId = cac.categoryId
                CROSS JOIN Budgets bi -- Attenzione: Stesso CROSS JOIN con Budgets qui
            GROUP BY
                t.transactionCategoryId,
                cac.categoryAllAmount
        )
    SELECT
        (SELECT COUNT(*) FROM CategoryTotals CT WHERE CT.categoryAllAmount < CT.ImportoCategoria) +
        (SELECT COUNT(*) FROM CategorySubcategorySums CS WHERE CS.subcategoryAllAmount < CS.ImportoSottocategoria)
""")
    suspend fun getPresenzaAllertInAccountOrCategory(accountId: Long, categoryId: Long?): Int
}