package com.example.activitytracker.data.repository

import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.local.sync.SyncStatus
import com.example.activitytracker.data.remote.ApiService
import com.example.activitytracker.data.remote.dto.ActivityUploadDto
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.time.LocalDate
import org.mockito.kotlin.eq
import java.util.UUID

class ActivityRepositoryTest {

    private lateinit var activityDao: ActivityDao
    private lateinit var apiService: ApiService
    private lateinit var repository: ActivityRepository

    @Before
    fun setUp() {
        activityDao = mock()
        apiService = mock()
        repository = ActivityRepository(activityDao, apiService)
    }

    @Test
    fun syncPendingActivities() = runTest {
        val userId = UUID.fromString("bb60ec8e-0cea-49a9-b393-a0d70bd3bb24")
        val entity = ActivityEntity(
            id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
            activityName = "Laufen",
            activityDate = LocalDate.of(2026, 5, 10),
            status = SyncStatus.PENDING_CREATE,
        )

        whenever(activityDao.getSyncWorkQue()).thenReturn(listOf(entity))
        whenever(
            apiService.saveActivity(
                eq(entity.id),
                eq(ActivityUploadDto(
                    activityName = "Laufen",
                    activityDate = "2026-05-10",
                    createdAt = entity.createdAt.toString(),
                    userId = userId
                ))
            )
        ).thenReturn(Response.success(Unit))

        repository.syncPendingActivities(userId)
        verify(activityDao).updateSyncStatus(eq(entity.id), eq(SyncStatus.SYNCED))
    }
}