package com.example.rgb.database.transactions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rgb.database.categories.CategoryEntity
import java.time.LocalDate

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
    val transactionId: Long = 0,

    @ColumnInfo(name = "transactionDescription")
    val transactionDescription: String? = null,

    @ColumnInfo(name = "transactionDate")
    val transactionDate: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "transactionSign")
    val transactionSign: Int = -1,

    @ColumnInfo(name = "transactionAmount")
    val transactionAmount: Double = 0.0,

    @ColumnInfo(name = "transactionCategoryId")
    val transactionCategoryId: Long? = null
)
