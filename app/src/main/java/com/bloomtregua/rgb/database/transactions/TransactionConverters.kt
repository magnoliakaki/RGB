package com.bloomtregua.rgb.database.transactions // Or your appropriate package

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
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

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString() // Salva come stringa ISO (es. "2023-10-27T10:15:30")
    }
}