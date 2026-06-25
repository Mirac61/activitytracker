package com.example.activitytracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.activitytracker.data.local.sync.SyncStatus
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

// Inspiration from https://developer.android.com/training/data-storage/room?hl=de
@Entity(tableName = "activity_entries")
data class ActivityEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val activityName: String,
    val activityDate: LocalDate,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val status: SyncStatus = SyncStatus.PENDING_CREATE
)