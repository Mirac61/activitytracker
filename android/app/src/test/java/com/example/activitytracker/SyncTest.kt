package com.example.activitytracker

import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.local.sync.SyncStatus
import com.example.activitytracker.data.remote.ApiService
import com.example.activitytracker.data.repository.ActivityRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.time.LocalDate
import java.time.OffsetDateTime

class SyncTest {

    private lateinit var dao: ActivityDao
    private lateinit var api: ApiService
    private lateinit var repository: ActivityRepository

    companion object {
        private const val USER_ID = "bb60ec8e-0cea-49a9-b393-a0d70bd3bb24"
    }

    @Before
    fun setUp() {
        dao = mock()
        api = mock()
        repository = ActivityRepository(dao, api)
    }

    private fun createEntity(id: String = "550e8400-e29b-41d4-a716-446655440000", name: String = "Laufen") =
        ActivityEntity(
            id = id,
            activityName = name,
            activityDate = LocalDate.of(2026, 5, 10),
            createdAt = OffsetDateTime.now(),
        )

    @Test
    fun successful_upload_sets_status_to_synced() = runTest {
        val entity = createEntity()
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(entity))
        whenever(api.uploadActivity(any())).thenReturn(Response.success(Unit))

        val hasError = repository.syncPendingActivities(USER_ID)

        assertFalse(hasError)
        verify(dao).updateSyncStatus(eq(entity.id), eq(SyncStatus.SYNCED))
    }

    @Test
    fun multiple_activities_all_synced_successfully() = runTest {
        val first = createEntity(id = "id-1", name = "Joggen")
        val second = createEntity(id = "id-2", name = "Radfahren")
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(first, second))
        whenever(api.uploadActivity(any())).thenReturn(Response.success(Unit))

        val hasError = repository.syncPendingActivities(USER_ID)

        assertFalse(hasError)
        verify(dao).updateSyncStatus(eq("id-1"), eq(SyncStatus.SYNCED))
        verify(dao).updateSyncStatus(eq("id-2"), eq(SyncStatus.SYNCED))
    }

    @Test
    fun network_error_returns_hasError_true() = runTest {
        val entity = createEntity()
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(entity))
        whenever(api.uploadActivity(any())).thenThrow(RuntimeException("No network"))

        val hasError = repository.syncPendingActivities(USER_ID)

        assertTrue(hasError)
        verify(dao, never()).updateSyncStatus(any(), any())
    }

    @Test
    fun server_error_does_not_set_synced() = runTest {
        val entity = createEntity()
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(entity))
        whenever(api.uploadActivity(any())).thenReturn(
            Response.error(500, okhttp3.ResponseBody.create(null, ""))
        )

        val hasError = repository.syncPendingActivities(USER_ID)

        assertFalse(hasError)
        verify(dao, never()).updateSyncStatus(any(), any())
    }

    @Test
    fun partial_failure_only_syncs_successful_activities() = runTest {
        val first = createEntity(id = "id-1", name = "Joggen")
        val second = createEntity(id = "id-2", name = "Radfahren")
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(first, second))

        whenever(api.uploadActivity(argThat { id == "id-1" }))
            .thenReturn(Response.success(Unit))
        whenever(api.uploadActivity(argThat { id == "id-2" }))
            .thenThrow(RuntimeException("Network error"))

        val hasError = repository.syncPendingActivities(USER_ID)

        assertTrue(hasError)
        verify(dao).updateSyncStatus(eq("id-1"), eq(SyncStatus.SYNCED))
        verify(dao, never()).updateSyncStatus(eq("id-2"), any())
    }

    @Test
    fun empty_queue_triggers_no_upload() = runTest {
        whenever(dao.getSyncWorkQue()).thenReturn(emptyList())

        val hasError = repository.syncPendingActivities(USER_ID)

        assertFalse(hasError)
        verify(api, never()).uploadActivity(any())
    }
}