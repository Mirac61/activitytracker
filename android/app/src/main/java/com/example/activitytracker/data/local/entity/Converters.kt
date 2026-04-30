package com.example.activitytracker.data.local.entity

import androidx.room.TypeConverter
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
}