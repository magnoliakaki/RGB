package com.example.rgb.database; // Or your appropriate package

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionConverters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromString(value: String?): LocalDate? {
        return value?.let {
            return formatter.parse(it, LocalDate::from)
        }
    }

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? {
        return date?.format(formatter)
    }
}