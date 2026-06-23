package com.example.activitytracker.data.repository

import com.example.activitytracker.data.local.dao.ReminderDao
import com.example.activitytracker.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import java.util.Calendar

class ReminderRepositoryTest {

    private lateinit var dao: ReminderDao
    private lateinit var repository: ReminderRepository

    @Before
    fun setUp() {
        dao = mock()
        repository = ReminderRepository(dao)
    }

    @Test
    fun insertAndGetAll() = runTest {
        val reminder = ReminderEntity(
            dayOfWeek = Calendar.WEDNESDAY,
            hour = 8,
            minute = 30,
            title = "Mein Titel",
            text = "Erinnerungstext",
            isDaily = true
        )
        whenever(dao.getAll()).thenReturn(flowOf(listOf(reminder)))
        whenever(dao.insert(reminder)).thenReturn(1L)

        val requestCode = repository.insert(reminder)
        val result = repository.getAll().first()[0]

        verify(dao).insert(reminder)
        assert(requestCode == 1L)
        assert(result.dayOfWeek == Calendar.WEDNESDAY)
        assert(result.hour == 8)
        assert(result.minute == 30)
        assert(result.title == "Mein Titel")
        assert(result.text == "Erinnerungstext")
        assert(result.isDaily)
    }
}
