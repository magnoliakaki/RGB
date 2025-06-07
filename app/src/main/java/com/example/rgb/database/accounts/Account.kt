package com.example.rgb.database.accounts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "accountId")
    val accountId: Int = 0,

    @ColumnInfo(name = "accountName")
    val accountName: String,

    @ColumnInfo(name = "accountType")
    val accountType: AccountType,

    @ColumnInfo(name = "accountIcon")
    val accountIcon: String,

    @ColumnInfo(name = "accountBalance")
    val accountBalance: Double
)
