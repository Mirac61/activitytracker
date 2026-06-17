package com.example.activitytracker.data.local.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.activitytracker.data.ActivityApplication
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.data.remote.RetrofitClient
import com.example.activitytracker.data.repository.ActivityRepository
import java.util.UUID
import java.util.concurrent.TimeUnit

// Inspiration from: https://developer.android.com/topic/architecture/data-layer/offline-first?hl=de

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    // Gathers required dependencies and syncs activities to the remote DB
    override suspend fun doWork(): Result {
        val app = applicationContext as? ActivityApplication ?: return Result.failure()
        val userId = app.authStorage.getUserId()?.let { UUID.fromString(it) } ?: return Result.failure()
        val database = AppDatabase.getInstance(applicationContext)
        val repository = ActivityRepository(database.activityDao(), RetrofitClient.api)
        val hasError = repository.syncPendingActivities(userId)
        return if (hasError) Result.retry() else Result.success()
    }

    companion object {
        // Builds a sync request that requires an active internet connection
        fun buildSyncRequest() = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .build()
    }
}