package com.example.activitytracker.data.remote

import com.example.activitytracker.data.remote.dto.FriendDto
import com.example.activitytracker.data.remote.dto.FriendRequestDto
import retrofit2.http.*
import java.util.UUID
import okhttp3.ResponseBody

interface FriendApiService {

    @GET("friends/{userId}")
    suspend fun getFriends(
            @Path("userId") userId: UUID
    ): List<FriendDto>

    @POST("friends/{userId}/request/{friendCode}")
    suspend fun sendFriendRequest(
            @Path("userId") userId: UUID,
            @Path("friendCode") friendCode: String
    )

    @GET("api/users/{userId}/friendcode")
    suspend fun getFriendCode(
        @Path("userId") userId: UUID
    ): ResponseBody

    @GET("friends/{userId}/requests")
    suspend fun getPendingRequests(
            @Path("userId") userId: UUID
    ): List<FriendRequestDto>

    @POST("friends/requests/{requestId}/accept")
    suspend fun acceptRequest(
            @Path("requestId") requestId: UUID
    )

    @POST("friends/requests/{requestId}/decline")
    suspend fun declineRequest(
            @Path("requestId") requestId: UUID
    )

    @DELETE("friends/{userId}/remove/{friendId}")
    suspend fun removeFriend(
            @Path("userId") userId: UUID,
            @Path("friendId") friendId: UUID
    )
}