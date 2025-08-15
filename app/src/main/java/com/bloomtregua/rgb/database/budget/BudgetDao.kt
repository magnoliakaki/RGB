package com.bloomtregua.rgb.database.budget

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    //Con il vincolo nella Entity ci dovrebbe essere sempre e solo 1 record in questa tabella, che indica l'impostazione globale dell'App
    @Query("SELECT * FROM budgets LIMIT 1")
    suspend fun getBudgetSettings(): BudgetEntity?
}