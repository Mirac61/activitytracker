package com.example.activitytracker.data.remote.dto


/*Builds requests and responses and sends them to the backend*/

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

