package com.example.activitytracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val requestCode: Int = 0,
    val dayOfWeek: Int,
    val hour: Int,
    val minute: Int,
    val title: String = "Zeit aktiv zu sein",
    val text: String,
    val isDaily: Boolean = false
)
