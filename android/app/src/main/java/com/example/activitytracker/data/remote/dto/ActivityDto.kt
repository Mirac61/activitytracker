package com.example.activitytracker.data.remote.dto

import com.example.activitytracker.data.local.entity.ActivityEntity
import java.util.UUID

data class ActivityUploadDto(
    val activityName: String,
    val activityDate: String,
    val createdAt: String,
    val userId: UUID
)

fun ActivityEntity.toUploadDto(userId: UUID) = ActivityUploadDto(
    activityName = activityName,
    activityDate = activityDate.toString(),
    createdAt = createdAt.toString(),
    userId = userId
)