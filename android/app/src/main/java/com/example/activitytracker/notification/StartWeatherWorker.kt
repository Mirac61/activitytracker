package com.example.activitytracker.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object StartWeatherWorker {
    fun scheduleWeatherUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<WeatherWorker>(1, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WeatherWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}