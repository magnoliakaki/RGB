package com.example.rgb.database.allocations

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AllocationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllocation(allocation: AllocationEntity): Int

    @Update
    suspend fun updateAllocation(allocation: AllocationEntity)

    @Delete
    suspend fun deleteAllocation(allocation: AllocationEntity)

    @Query("SELECT * FROM allocations")
    fun getAllAllocations(): Flow<List<AllocationEntity>>

    @Query("SELECT * FROM allocations WHERE allocationId = :allocationId")
    fun getAllocationById(allocationId: Int): Flow<AllocationEntity>
}