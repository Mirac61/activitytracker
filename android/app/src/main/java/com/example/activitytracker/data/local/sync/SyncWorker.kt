package com.example.activitytracker.data.local.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.data.remote.RetrofitClient
import com.example.activitytracker.data.repository.ActivityRepository


// Inspiration from: https://developer.android.com/topic/architecture/data-layer/offline-first?hl=de

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(applicationContext)
        val repository = ActivityRepository(database.activityDao(), RetrofitClient.apiService)

        val hasError = repository.syncPendingActivities()
        return if (hasError) Result.retry() else Result.success()
    }
    companion object {
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<SyncWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .build()

        val SyncConstraints
            get() = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
    }
}