package com.example.activitytracker.data.local.converter

import androidx.room.TypeConverter
import com.example.activitytracker.data.local.sync.SyncStatus

class SyncStatusConverter {

    @TypeConverter
    fun fromStatus(status: SyncStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }
}