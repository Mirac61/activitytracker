package com.example.activitytracker.data.remote

import com.example.activitytracker.data.remote.dto.ActivityUploadDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("activities/upload")
    suspend fun uploadActivity(
        @Body activity: ActivityUploadDto
    ): Response<Unit>

    @PUT("activities/{id}")
    suspend fun updateActivity(
        @Path("id") id: String,
        @Body activity: ActivityUploadDto
    ): Response<Unit>
}