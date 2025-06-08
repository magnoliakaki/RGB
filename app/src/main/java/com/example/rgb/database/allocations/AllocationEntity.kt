package com.example.rgb.database.allocations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "allocations")
data class AllocationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "allocationId")
    val allocationId: Int = 0,

    @ColumnInfo(name = "allocationEndDate")
    val allocationEndDate: Date,

    @ColumnInfo(name = "allocationAmount")
    val allocationAmount: Double,

    @ColumnInfo(name = "allocationFrequencyType")
    val allocationFrequencyType: String,

    @ColumnInfo(name = "allocationFrequencyValue")
    val allocationFrequencyValue: Int
)
