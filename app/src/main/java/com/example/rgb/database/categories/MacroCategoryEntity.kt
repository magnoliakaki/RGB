package com.example.rgb.database.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "macrocategories")
data class MacroCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "macroCategoryId")
    val macroCategoryId: Int = 0,

    @ColumnInfo(name = "macroCategoryName")
    val macroCategoryName: String
)
