package com.example.activitytracker.data.remote

import com.example.activitytracker.data.remote.dto.ActivityUploadDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("activities/upload")
    suspend fun uploadActivity(@Body activity: ActivityUploadDto): Response<Unit>
}