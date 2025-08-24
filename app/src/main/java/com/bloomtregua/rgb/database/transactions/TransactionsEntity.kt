package com.bloomtregua.rgb.database.transactions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bloomtregua.rgb.database.categories.CategoryEntity
import com.bloomtregua.rgb.database.categories.SubcategoryEntity
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "transactions",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["transactionCategoryId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        androidx.room.ForeignKey(
            entity = SubcategoryEntity::class,
            parentColumns = ["subcategoryId"],
            childColumns = ["transactionSubCategoryId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        androidx.room.ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["transactionMacroCategoryId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index("transactionCategoryId"), androidx.room.Index("transactionSubCategoryId")
])
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transactionId")
    val transactionId: Long = 0,

    @ColumnInfo(name = "transactionDescription")
    val transactionDescription: String? = null,

    @ColumnInfo(name = "transactionDate")
    val transactionDate: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "transactionTimestamp") // NUOVO CAMPO per gestire oltre alla data anche l'ora di inserimento
    val transactionTimestamp: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "transactionSign")       // Indica se è un ingresso o una uscita di soldi. -1 uscita, 1 entrata
    val transactionSign: Int = -1,

    @ColumnInfo(name = "transactionAmount")
    val transactionAmount: Double = 0.0,

    @ColumnInfo(name = "transactionCategoryId")
    val transactionCategoryId: Long? = null,

    @ColumnInfo(name = "transactionSubCategoryId")
    val transactionSubCategoryId: Long? = null,

    @ColumnInfo(name = "transactionMacroCategoryId")
    val transactionMacroCategoryId: Long? = null,

    @ColumnInfo(name = "transactionDaContabilizzare", defaultValue = "0") // Default a false (non da contabilizzare inizialmente)
    val transactionDaContabilizzare: Boolean = false
)
