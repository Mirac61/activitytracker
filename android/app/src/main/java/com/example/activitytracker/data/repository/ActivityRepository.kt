package com.example.activitytracker.data.repository

import androidx.annotation.WorkerThread
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

//Interface between Viewmodel and Database
class ActivityRepository(private val activityDao: ActivityDao) {

    val getAll: Flow<List<ActivityEntity>> = activityDao.getAll()

    @WorkerThread
    suspend fun insert(entry: ActivityEntity) {
        activityDao.insert(entry)
    }
}