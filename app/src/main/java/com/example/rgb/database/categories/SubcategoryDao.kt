package com.example.rgb.database.categories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubcategoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSubcategory(subcategory: SubcategoryEntity): Int

    @Update
    suspend fun updateSubcategory(subcategory: SubcategoryEntity)

    @Delete
    suspend fun deleteSubcategory(subcategory: SubcategoryEntity)

    @Query("SELECT * FROM subcategories")
    fun getAllSubcategories(): Flow<List<SubcategoryEntity>>

    @Query("SELECT * FROM subcategories WHERE subcategoryId = :subcategoryId")
    fun getSubcategoryById(subcategoryId: Int): Flow<SubcategoryEntity>
}