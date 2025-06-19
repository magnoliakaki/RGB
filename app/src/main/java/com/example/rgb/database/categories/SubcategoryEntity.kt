package com.example.rgb.database.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subcategories",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["subcategoryCategoryId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
        ],
    indices = [androidx.room.Index("categoryId")
    ])
data class SubcategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subcategoryId")
    val subcategoryId: Int = 0,

    @ColumnInfo(name = "subcategoryName")
    val subcategoryName: String,

    @ColumnInfo(name = "subcategoryCategoryId")
    val subcategoryCategoryId: Int? = null
)
