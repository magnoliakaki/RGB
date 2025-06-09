package com.example.rgb.database.transactions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rgb.database.categories.CategoryEntity
import java.util.Date

@Entity(tableName = "transactions",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["transactionCategoryId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
    ],
    indices = [
        androidx.room.Index("transactionCategoryId")
])
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transactionId")
    val transactionId: Int = 0,

    @ColumnInfo(name = "transactionDescription")
    val transactionDescription: String,

    @ColumnInfo(name = "transactionDate")
    val transactionDate: Date,

    @ColumnInfo(name = "transactionSign")
    val transactionSign: String,

    @ColumnInfo(name = "transactionAmount")
    val transactionAmount: Double,

    @ColumnInfo(name = "transactionCategoryId")
    val transactionCategoryId: Int
)
