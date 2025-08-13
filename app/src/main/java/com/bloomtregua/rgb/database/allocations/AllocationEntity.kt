package com.bloomtregua.rgb.database.allocations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allocations")
data class AllocationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "allocationId")
    val allocationId: Long = 0,

    @ColumnInfo(name = "allocationName")
    val allocationName: String
)
