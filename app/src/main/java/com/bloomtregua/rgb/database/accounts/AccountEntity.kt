package com.bloomtregua.rgb.database.accounts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "accountId")
    val accountId: Long = 0,

    @ColumnInfo(name = "accountName")
    val accountName: String,

    @ColumnInfo(name = "accountType")
    val accountType: AccountType = AccountType.CHECKING,

    @ColumnInfo(name = "accountIcon")
    val accountIcon: String? = null,

    @ColumnInfo(name = "accountBalance")
    val accountBalance: Double = 0.0
)
