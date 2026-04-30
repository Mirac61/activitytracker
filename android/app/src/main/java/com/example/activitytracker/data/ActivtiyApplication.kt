package com.example.activitytracker.data

import android.app.Application
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.data.repository.ActivityRepository

class ActivityApplication : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { ActivityRepository(database.activityDao()) }
}