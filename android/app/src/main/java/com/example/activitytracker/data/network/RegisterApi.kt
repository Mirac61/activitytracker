package com.example.activitytracker.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

//Input data that will be sent to backend
data class RegisterRequest(
    val vorname: String,
    val nachname: String,
    val email: String,
    val password: String
)

//Api call to backend
interface RegisterApi {
    @POST("api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<Unit>
}