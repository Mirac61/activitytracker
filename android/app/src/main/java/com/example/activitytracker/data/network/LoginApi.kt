package com.example.activitytracker.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

//Input data that will be sent to backend
data class LoginRequest(
    val email: String,
    val password: String
)
data class LoginResponse(val userId: String)

//Api call to backend
interface LoginApi {
    @POST("api/users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}