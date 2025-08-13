package com.bloomtregua.rgb.database.accounts

import androidx.room.TypeConverter

class AccountConverters {
    @TypeConverter
    fun fromAccountType(value: AccountType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toAccountType(value: String?): AccountType? {
        return value?.let { AccountType.valueOf(it) }
    }
}