package com.example.activitytracker.data.repository

import androidx.annotation.WorkerThread
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.local.sync.SyncStatus
import com.example.activitytracker.data.remote.ApiService
import com.example.activitytracker.data.remote.dto.toUploadDto
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

interface IActivityRepository {
    suspend fun insert(entity: ActivityEntity)

    suspend fun syncPendingActivities(userId: UUID): Boolean

    suspend fun update(entity: ActivityEntity)

    val getAll: Flow<List<ActivityEntity>>

    val getDates: Flow<List<LocalDate>>

    val getActivityNames: Flow<List<String>>

    val getStatistics: Flow<List<LocalDate>>
}


//Interface between Viewmodel and Database
class ActivityRepository(private val activityDao: ActivityDao, private val apiService: ApiService) : IActivityRepository {

    override val getAll: Flow<List<ActivityEntity>> = activityDao.getAll()
    override val getDates: Flow<List<LocalDate>> = activityDao.getDates()
    override val getActivityNames: Flow<List<String>> = activityDao.getActivityNames()
    override val getStatistics: Flow<List<LocalDate>> = activityDao.getStatistics()


    @WorkerThread
    override suspend fun insert(entity: ActivityEntity) {
        activityDao.insert(
            entity.copy(status = SyncStatus.PENDING_CREATE)
        )
    }

    @WorkerThread
    override suspend fun update(entity: ActivityEntity) {
        activityDao.update(
            entity.copy(status = SyncStatus.PENDING_UPDATE)
        )
    }

    override suspend fun syncPendingActivities(userId: UUID): Boolean {

        val que = activityDao.getSyncWorkQue()
        var hasError = false

        for (activity in que) {
            try {
                val response = when (activity.status) {
                    SyncStatus.PENDING_CREATE, SyncStatus.PENDING_UPDATE -> {
                        apiService.saveActivity(
                            id = activity.id,
                            activity = activity.toUploadDto(userId)
                        )
                    }

                    SyncStatus.PENDING_DELETE -> {
                        // for a future feature
                        hasError = true
                        continue
                    }

                    SyncStatus.SYNCED -> {
                        continue
                    }
                }

                if (response.isSuccessful) {
                    activityDao.updateSyncStatus(activity.id, SyncStatus.SYNCED)
                } else {
                    hasError = true
                }
            } catch (_: Exception) {
                hasError = true
            }
        }

        return hasError
    }
}