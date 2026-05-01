package com.example.activitytracker

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.repository.IActivityRepository
import com.example.activitytracker.ui.screens.tracking.TrackingViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// Inspiration from https://developer.android.com/training/data-storage/room/testing-db?hl=de

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var activityDao: ActivityDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).allowMainThreadQueries().build()
        activityDao = db.activityDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getAllReturnsEmptyWhenEmpty() = runBlocking{
        val result = activityDao.getAll().first()
        assert(result.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun insertAndReadActivity() = runBlocking{
        val activity = ActivityEntity(
            activityName = "Jogging",
            activityDate = java.time.LocalDate.of(2026, 5, 1),
            createdAt = java.time.OffsetDateTime.parse("2026-05-01T15:00:00Z"),
            userId = "mock_user_1"
        )

        activityDao.insert(activity)
        val allActivities = activityDao.getAll().first()

        assert(allActivities.isNotEmpty())
        assert(allActivities[0].activityName == "Jogging")
        assert(allActivities[0].activityDate == java.time.LocalDate.of(2026, 5, 1))
        assert(allActivities[0].userId == "mock_user_1")
    }

    @Test
    @Throws(Exception::class)
    fun findByIdReturnsCorrectActivity() = runBlocking{

        val firstActivity = ActivityEntity(
            activityName = "Jogging",
            activityDate = java.time.LocalDate.of(2026, 2, 1),
            createdAt = java.time.OffsetDateTime.parse("2026-06-01T10:00:00Z"),
            userId = "mock_user_1"
        )
        val secondActivity = ActivityEntity(
            activityName = "Rad fahren",
            activityDate = java.time.LocalDate.of(2026, 1, 21),
            createdAt = java.time.OffsetDateTime.parse("2026-07-01T10:00:00Z"),
            userId = "mock_user_2"
        )

        activityDao.insert(firstActivity)
        activityDao.insert(secondActivity)

        val allActivities = activityDao.getAll().first()
        val secondId = allActivities[0].id
        val found = activityDao.findById(secondId)

        assert(found != null)
        assert(found?.activityName == "Rad fahren")
        assert(allActivities[0].activityDate == java.time.LocalDate.of(2026, 1, 21))
    }

    @Test
    @Throws(Exception::class)
    fun findByIdReturnsNullWhenMissing() = runBlocking{
        val result = activityDao.getAll().first()
        assert(result.isEmpty())

        val found = activityDao.findById("99")
        assert(found == null)
    }

    @Test
    @Throws(Exception::class)
    fun deleteInsertedActivity() = runBlocking{
        val activity = ActivityEntity(
            activityName = "Jogging",
            activityDate = java.time.LocalDate.of(2026, 4, 21),
            createdAt = java.time.OffsetDateTime.parse("2026-08-21T10:00:00Z"),
            userId = "mock_user_1"
        )

        activityDao.insert(activity)

        val allActivities = activityDao.getAll().first()
        val id = allActivities[0].id
        val found = activityDao.findById(id)

        activityDao.delete(found!!)

        assert(activityDao.getAll().first().isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun deleteNonExistentDoesNotCrash() = runBlocking{

        val fakeActivity = ActivityEntity(
            id="999",
            activityName = "Jogging",
            activityDate = java.time.LocalDate.of(2021, 8, 21),
            createdAt = java.time.OffsetDateTime.parse("2026-08-21T10:00:00Z"),
            userId = "mock_user_1"
        )

        activityDao.delete(fakeActivity)

        val all = activityDao.getAll().first()
        assert(all.isEmpty())

    }

    @Test
    fun saveActivity_withEmptyName_doesNotInsertToRepository() = runBlocking {
        var insertCount = 0

        val fakeRepository = object : IActivityRepository {
            override val getAll: kotlinx.coroutines.flow.Flow<List<ActivityEntity>> =
                kotlinx.coroutines.flow.flowOf(emptyList())

            override suspend fun insert(entity: ActivityEntity) {
                insertCount++
            }
        }

        val viewModel = TrackingViewModel(fakeRepository)
        viewModel.saveActivity("", java.time.LocalDate.now())

        assert(insertCount == 0)
    }
}