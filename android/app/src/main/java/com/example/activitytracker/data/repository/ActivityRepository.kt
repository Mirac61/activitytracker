package com.example.activitytracker.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.local.sync.SyncStatus
import com.example.activitytracker.data.remote.ApiService
import com.example.activitytracker.data.remote.dto.toUploadDto
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface IActivityRepository {
    suspend fun insert(entity: ActivityEntity)

    suspend fun syncPendingActivities(userId: String): Boolean

    val getAll: Flow<List<ActivityEntity>>

    val getDates: Flow<List<LocalDate>>

    val getActivityNames: Flow<List<String>>
}


//Interface between Viewmodel and Database
class ActivityRepository(private val activityDao: ActivityDao, private val apiService: ApiService) : IActivityRepository {

    override val getAll: Flow<List<ActivityEntity>> = activityDao.getAll()
    override val getDates: Flow<List<LocalDate>> = activityDao.getDates()
    override val getActivityNames: Flow<List<String>> = activityDao.getActivityNames()


    @WorkerThread
    override suspend fun insert(entity: ActivityEntity) {
        activityDao.insert(entity)
    }

    override suspend fun syncPendingActivities(userId: String): Boolean{

        val que = activityDao.getSyncWorkQue()
        var hasError = false

        for (activity in que) {
            try{
                val response = apiService.uploadActivity(activity.toUploadDto(userId))

                if (response.isSuccessful){
                    activityDao.updateSyncStatus(activity.id, SyncStatus.SYNCED)
                }else{
                    hasError = true
                }
            }
            catch (e: Exception){
                hasError = true
            }
        }
        return hasError
    }
}