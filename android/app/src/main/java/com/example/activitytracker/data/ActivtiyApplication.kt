package com.example.activitytracker.data

import android.app.Application
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.data.local.storage.AuthStorage
import com.example.activitytracker.data.remote.RetrofitClient
import com.example.activitytracker.data.repository.ActivityRepository
import com.example.activitytracker.data.repository.ReminderRepository

/* Provides app-wide dependencies. */
class ActivityApplication : Application() {
    private val database by lazy { AppDatabase.getInstance(this) }
    val authStorage by lazy { AuthStorage(this) }
    val repository by lazy { ActivityRepository(database.activityDao(),
        RetrofitClient.api) }
    val reminderRepository by lazy { ReminderRepository(database.reminderDao()) }

}
