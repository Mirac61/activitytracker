package com.example.activitytracker.data.remote.dto

import java.util.UUID

data class FriendDto(
    val friendId: UUID,
    val username: String,
    val friendCode: String
)

data class FriendRequestDto(
    val requestId: UUID,
    val senderUsername: String,
    val senderFriendCode: String
)