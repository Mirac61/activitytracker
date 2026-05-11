package com.example.activitytracker.data.repository

import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.local.sync.SyncStatus
import com.example.activitytracker.data.remote.ApiService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.time.LocalDate
import org.mockito.kotlin.eq

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
    fun syncPendingActivities() = runTest{
        val entity = ActivityEntity(
            id = "550e8400-e29b-41d4-a716-446655440000",
            activityName = "Laufen",
            activityDate = LocalDate.of(2026, 5, 10),
            status = SyncStatus.PENDING
        )

        whenever(activityDao.getSyncWorkQue()).thenReturn(listOf(entity))
        whenever(apiService.uploadActivity(any())).thenReturn(Response.success(Unit))
        repository.syncPendingActivities()
        verify(activityDao).updateSyncStatus(eq(entity.id), eq(SyncStatus.SYNCED))
    }
}