package com.example.activitytracker.data.local.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Converters {
    // Nutzt den internationalen Standard: "2026-04-30T16:45:00+02:00"
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): OffsetDateTime? {
        return value?.let { OffsetDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun fromLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? {
        return date?.toString()
    }
}