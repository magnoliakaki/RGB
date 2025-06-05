package com.example.rgb.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromAccountType(value: AccountType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toAccountType(value: String?): AccountType? {
        return value?.let { AccountType.valueOf(it) }
    }
}