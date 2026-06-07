package com.example.activitytracker.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleLoginRequest(
    @SerializedName("idToken") val idToken: String
)

data class GoogleLoginResponse(
    val userId: String,
    val accessToken: String,
    val refreshToken: String
)