package com.example.activitytracker.data.remote.dto



/*Builds requests and responses and sends them to the backend*/
data class RegisterRequest(
    val vorname: String,
    val nachname: String,
    val email: String,
    val password: String
)
data class AuthResponse(val userId: String)
