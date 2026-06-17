package com.example.activitytracker.data.remote

import com.example.activitytracker.data.remote.dto.AuthResponse
import com.example.activitytracker.data.remote.dto.LoginRequest
import com.example.activitytracker.data.remote.dto.LoginResponse
import com.example.activitytracker.data.remote.dto.RefreshRequest
import com.example.activitytracker.data.remote.dto.RegisterRequest
import com.example.activitytracker.data.remote.dto.ActivityUploadDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

// Echo API Calls

interface ApiService {

    @PUT("activities/{id}")
    suspend fun saveActivity(
        @Path("id") id: UUID,
        @Body activity: ActivityUploadDto
    ): Response<Unit>

    @POST("api/users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<LoginResponse>

    @POST("api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("/api/weather")
    fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double): Call<ResponseBody>
}