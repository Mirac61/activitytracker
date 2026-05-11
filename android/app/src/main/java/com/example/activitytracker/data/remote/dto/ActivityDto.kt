package com.example.activitytracker.data.remote.dto

import com.example.activitytracker.data.local.entity.ActivityEntity

data class ActivityUploadDto(
    val id: String,
    val activityName: String,
    val activityDate: String
)

fun ActivityEntity.toUploadDto() = ActivityUploadDto(
    id = id,
    activityName = activityName,
    activityDate = activityDate.toString()
)