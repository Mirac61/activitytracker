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
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import okhttp3.ResponseBody.Companion.toResponseBody

class SyncTest {

    private lateinit var dao: ActivityDao
    private lateinit var api: ApiService
    private lateinit var repository: ActivityRepository

    companion object {
        private val USER_ID: UUID = UUID.fromString("bb60ec8e-0cea-49a9-b393-a0d70bd3bb24")
        private val ID_1: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val ID_2: UUID = UUID.fromString("22222222-2222-2222-2222-222222222222")
    }

    @Before
    fun setUp() {
        dao = mock()
        api = mock()
        repository = ActivityRepository(dao, api)
    }

    private fun createEntity(
        id: UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
        name: String = "Laufen"
    ) = ActivityEntity(
            id = id,
            activityName = name,
            activityDate = LocalDate.of(2026, 5, 10),
            createdAt = OffsetDateTime.now(),
        )

    @Test
    fun successful_upload_sets_status_to_synced() = runTest {
        val entity = createEntity()
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(entity))
        whenever(api.saveActivity( any(), any())).thenReturn(Response.success(Unit))

        val hasError = repository.syncPendingActivities(USER_ID)

        assertFalse(hasError)
        verify(dao).updateSyncStatus(eq(entity.id), eq(SyncStatus.SYNCED))
    }

    @Test
    fun multiple_activities_all_synced_successfully() = runTest {
        val first = createEntity(id = ID_1, name = "Joggen")
        val second = createEntity(id = ID_2, name = "Radfahren")
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(first, second))
        whenever(api.saveActivity(any(),any())).thenReturn(Response.success(Unit))

        val hasError = repository.syncPendingActivities(USER_ID)

        assertFalse(hasError)
        verify(dao).updateSyncStatus(eq(ID_1), eq(SyncStatus.SYNCED))
        verify(dao).updateSyncStatus(eq(ID_2), eq(SyncStatus.SYNCED))
    }

    @Test
    fun network_error_returns_hasError_true() = runTest {
        val entity = createEntity()
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(entity))
        whenever(api.saveActivity(any(), any())).thenThrow(RuntimeException("No network"))

        val hasError = repository.syncPendingActivities(USER_ID)

        assertTrue(hasError)
        verify(dao, never()).updateSyncStatus(any(), any())
    }

    @Test
    fun server_error_does_not_set_synced() = runTest {
        val entity = createEntity()
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(entity))
        whenever(api.saveActivity(any(),any())).thenReturn(
            Response.error(500, "".toResponseBody(null))
        )

        val hasError = repository.syncPendingActivities(USER_ID)

        assertTrue(hasError)
        verify(dao, never()).updateSyncStatus(any(), any())
    }

    @Test
    fun partial_failure_only_syncs_successful_activities() = runTest {
        val first = createEntity(id = ID_1, name = "Joggen")
        val second = createEntity(id = ID_2, name = "Radfahren")
        whenever(dao.getSyncWorkQue()).thenReturn(listOf(first, second))

        whenever(api.saveActivity(eq(ID_1), any()))
            .thenReturn(Response.success(Unit))
        whenever(api.saveActivity(eq(ID_2), any()))
            .thenThrow(RuntimeException("Network error"))

        val hasError = repository.syncPendingActivities(USER_ID)

        assertTrue(hasError)
        verify(dao).updateSyncStatus(eq(ID_1), eq(SyncStatus.SYNCED))
        verify(dao, never()).updateSyncStatus(eq(ID_2), any())
    }

    @Test
    fun empty_queue_triggers_no_upload() = runTest {
        whenever(dao.getSyncWorkQue()).thenReturn(emptyList())

        val hasError = repository.syncPendingActivities(USER_ID)

        assertFalse(hasError)
        verify(api, never()).saveActivity(any(),any())
    }
}