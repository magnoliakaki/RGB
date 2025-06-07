package com.example.rgb.database.transactions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
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
)
