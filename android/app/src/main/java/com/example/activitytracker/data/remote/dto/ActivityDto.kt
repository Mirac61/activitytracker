package com.example.activitytracker.data.remote.dto

import com.example.activitytracker.data.local.entity.ActivityEntity

data class ActivityUploadDto(
    val id: String,
    val activityName: String,
    val activityDate: String,
    val createdAt: String,
    val userId: String
)

fun ActivityEntity.toUploadDto(userId: String) = ActivityUploadDto(
    id = id,
    activityName = activityName,
    activityDate = activityDate.toString(),
    createdAt = createdAt.toString(),
    userId = userId
)