package com.example.rgb.database.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subcategories")
data class SubcategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subcategoryId")
    val subcategoryId: Int = 0,

    @ColumnInfo(name = "subcategoryName")
    val subcategoryName: String,
)
