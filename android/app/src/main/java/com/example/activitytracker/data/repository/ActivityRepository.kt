package com.example.activitytracker.data.repository

import androidx.annotation.WorkerThread
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface IActivityRepository {
    suspend fun insert(entity: ActivityEntity)

    val getAll: Flow<List<ActivityEntity>>

    val getDates: Flow<List<LocalDate>>
}


//Interface between Viewmodel and Database
class ActivityRepository(private val activityDao: ActivityDao) : IActivityRepository {

    override val getAll: Flow<List<ActivityEntity>> = activityDao.getAll()
    override val getDates: Flow<List<LocalDate>> = activityDao.getDates()

    @WorkerThread
    override suspend fun insert(entity: ActivityEntity) {
        activityDao.insert(entity)
    }
}