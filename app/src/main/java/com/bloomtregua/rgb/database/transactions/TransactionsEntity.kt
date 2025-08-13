package com.bloomtregua.rgb.database.transactions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bloomtregua.rgb.database.categories.CategoryEntity
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

    @ColumnInfo(name = "transactionSign")       // Indica se è un ingresso o una uscita di soldi. -1 uscita, 1 entrata
    val transactionSign: Int = -1,

    @ColumnInfo(name = "transactionAmount")
    val transactionAmount: Double = 0.0,

    @ColumnInfo(name = "transactionCategoryId")
    val transactionCategoryId: Long? = null
)
