package com.example.activitytracker.data.repository

import androidx.annotation.WorkerThread
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntry
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDao: ActivityDao) {

    val getAll: Flow<List<ActivityEntry>> = activityDao.getAll()

    @WorkerThread
    suspend fun insert(entry: ActivityEntry) {
        activityDao.insert(entry)
    }
}