package com.example.rgb.database.categories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MacroCategoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMacroCategory(macroCategory: MacroCategoryEntity): Long

    @Update
    suspend fun updateMacroCategory(macroCategory: MacroCategoryEntity)

    @Delete
    suspend fun deleteMacroCategory(macroCategory: MacroCategoryEntity)

    @Query("SELECT * FROM macrocategories")
    fun getAllMacroCategories(): Flow<List<MacroCategoryEntity>>

    @Query("SELECT * FROM macrocategories WHERE macroCategoryId = :macroCategoryId")
    fun getMacroCategoryById(macroCategoryId: Int): Flow<MacroCategoryEntity>
}