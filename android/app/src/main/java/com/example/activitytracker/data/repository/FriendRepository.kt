package com.example.activitytracker.data.repository

import com.example.activitytracker.data.remote.RetrofitClient
import com.example.activitytracker.data.remote.dto.FriendDto
import com.example.activitytracker.data.remote.dto.FriendRequestDto
import java.util.UUID

class FriendRepository {

    private val api = RetrofitClient.friendApi

    // ─── Freundesliste laden ──────────────────────────────────────

    suspend fun getFriends(userId: UUID): Result<List<FriendDto>> {
        return try {
            val friends = api.getFriends(userId)
            Result.success(friends)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Anfrage senden ───────────────────────────────────────────

    suspend fun sendFriendRequest(userId: UUID, friendCode: String): Result<Unit> {
        return try {
            api.sendFriendRequest(userId, friendCode)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFriendCode(userId: UUID): Result<String> {
        return try {
            val responseBody = api.getFriendCode(userId)
            Result.success(responseBody.string())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Offene Anfragen laden ────────────────────────────────────

    suspend fun getPendingRequests(userId: UUID): Result<List<FriendRequestDto>> {
        return try {
            val requests = api.getPendingRequests(userId)
            Result.success(requests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Anfrage annehmen ─────────────────────────────────────────

    suspend fun acceptRequest(requestId: UUID): Result<Unit> {
        return try {
            api.acceptRequest(requestId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Anfrage ablehnen ─────────────────────────────────────────

    suspend fun declineRequest(requestId: UUID): Result<Unit> {
        return try {
            api.declineRequest(requestId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Freund entfernen ─────────────────────────────────────────

    suspend fun removeFriend(userId: UUID, friendId: UUID): Result<Unit> {
        return try {
            api.removeFriend(userId, friendId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}