package com.example.activitytracker.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val userId: String,
    val accessToken: String,
    val refreshToken: String)

data class RefreshRequest(
    val refreshToken: String
)

interface LoginApi {
    @POST("api/users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<LoginResponse>
}