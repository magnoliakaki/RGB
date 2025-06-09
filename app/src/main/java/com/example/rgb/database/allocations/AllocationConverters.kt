package com.example.rgb.database.allocations

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class DateTimeConverters {
    private val zone: ZoneId = ZoneId.systemDefault()

    // LocalDateTime ↔ Long (millis)
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): Long? =
        value?.atZone(zone)?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toLocalDateTime(value: Long?): LocalDateTime? =
        value
            ?.let { Instant.ofEpochMilli(it) }
            ?.atZone(zone)
            ?.toLocalDateTime()

    // LocalDate ↔ Int (giorni dall’epoch)
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Int? =
        value?.toEpochDay()?.toInt()

    @TypeConverter
    fun toLocalDate(value: Int?): LocalDate? =
        value
            ?.toLong()
            ?.let { LocalDate.ofEpochDay(it) }
}
