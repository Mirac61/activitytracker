package com.example.activitytracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Inspiration from https://developer.android.com/training/data-storage/room?hl=de
@Entity(tableName = "activity_entries")
data class ActivityEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val createdAt: Long, // Room has no Date, so Long -> Date in a Mapper or Repository
    val userId: String? = null // For future Keycloak integration, currently mocked
)