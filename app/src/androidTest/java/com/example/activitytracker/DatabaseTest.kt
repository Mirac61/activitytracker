package com.example.activitytracker

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

class DatabaseTest {

    @RunWith(AndroidJUnit4::class)
    class SimpleEntityReadWriteTest {
        private lateinit var activityDao: ActivityDao
        private lateinit var db: AppDatabase

        @Before
        fun createDb() {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java).build()
            activityDao = db.activityDao()
        }

        @After
        fun closeDb() {
            db.close()
        }

        @Test
        @Throws(Exception::class)
        fun getAllReturnsEmptyWhenEmpty(){
            val result = activityDao.getAll()
            assert(result.isEmpty())
        }

        @Test
        @Throws(Exception::class)
        fun insertAndReadActivity() {
            val activity = ActivityEntry(
                name = "Jogging",
                createdAt = System.currentTimeMillis(),
                userId = "mock_user_1"
            )

            activityDao.insertAll(activity)
            val allActivities = activityDao.getAll()

            assert(allActivities.isNotEmpty())
            assert(allActivities[0].name == "Jogging")
            assert(allActivities[0].userId == "mock_user_1")
        }

        @Test
        @Throws(Exception::class)
        fun findByIdReturnsCorrectActivity() {

            val firstActivity = ActivityEntry(
                name = "Jogging",
                createdAt = 10000,
                userId = "mock_user_1"
            )
            val secondActivity = ActivityEntry(
                name = "Rad fahren",
                createdAt = 20000,
                userId = "mock_user_2"
            )

            activityDao.insertAll(firstActivity, secondActivity)

            val allActivities = activityDao.getAll()
            val secondId = allActivities[1].id
            val found = activityDao.findById(secondId)

            assert(found != null)
            assert(found?.name == "Rad fahren")
        }

        @Test
        @Throws(Exception::class)
        fun findByIdReturnsNullWhenMissing(){
            val result = activityDao.getAll()
            assert(result.isEmpty())

            val found = activityDao.findById(99)
            assert(found == null)
        }

        @Test
        @Throws(Exception::class)
        fun deleteInsertedActivity() {
            val activity = ActivityEntry(
                name = "Jogging",
                createdAt = 10000,
                userId = "mock_user_1"
            )

            activityDao.insertAll(activity)

            val allActivities = activityDao.getAll()
            val id = allActivities[0].id
            val found = activityDao.findById(id)

            activityDao.delete(found!!)

            assert(activityDao.getAll().isEmpty())
        }

        @Test
        @Throws(Exception::class)
        fun deleteNonExistentDoesNotCrash() {

            val fakeActivity = ActivityEntry(
                id=999,
                name = "Jogging",
                createdAt = 10000,
                userId = "mock_user_1"
            )

            activityDao.delete(fakeActivity)

            val all = activityDao.getAll()
            assert(all.isEmpty())

        }


    }
}