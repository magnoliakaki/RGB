package com.example.rgb.database.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rgb.database.allocations.AllocationEntity

@Entity(tableName = "macrocategories",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = AllocationEntity::class,
            parentColumns = ["allocationId"],
            childColumns = ["macroCategoryAllocationId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
            indices = [androidx.room.Index("allocationId")]
)
data class MacroCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "macroCategoryId")
    val macroCategoryId: Int = 0,

    @ColumnInfo(name = "macroCategoryName")
    val macroCategoryName: String,

    @ColumnInfo(name = "macroCategoryAllocationId")
    val macroCategoryAllocationId: Int
)
