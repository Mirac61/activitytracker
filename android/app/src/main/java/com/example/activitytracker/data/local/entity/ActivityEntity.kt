package com.example.activitytracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime
import java.util.UUID

// Inspiration from https://developer.android.com/training/data-storage/room?hl=de
@Entity(tableName = "activity_entries")
data class ActivityEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
//    val createdAt: Long, // Room has no Date, so Long -> Date in a Mapper or Repository
    val userId: String? = null // For future Keycloak integration, currently mocked
){
    init {
        require(name.isNotBlank()) { "Name darf nicht leer sein!" }
    }
}