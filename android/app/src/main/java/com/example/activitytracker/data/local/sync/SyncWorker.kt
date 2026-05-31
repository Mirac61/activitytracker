package com.example.activitytracker.data.local.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.activitytracker.data.ActivityApplication
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.data.remote.RetrofitClient
import com.example.activitytracker.data.repository.ActivityRepository
import java.util.concurrent.TimeUnit

// Inspiration from: https://developer.android.com/topic/architecture/data-layer/offline-first?hl=de

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? ActivityApplication ?: return Result.failure()
        val userId = app.authStorage.getUserId() ?: return Result.failure()
        val database = AppDatabase.getInstance(applicationContext)
        val repository = ActivityRepository(database.activityDao(), RetrofitClient.instance)
        val hasError = repository.syncPendingActivities(userId)
        return if (hasError) Result.retry() else Result.success()
    }

    companion object {
        fun buildSyncRequest() = OneTimeWorkRequestBuilder<SyncWorker>()
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .build()
    }
}