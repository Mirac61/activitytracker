package com.example.activitytracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntry

@Database(entities = [ActivityEntry::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    /* Note: Migrations are currently handled via fallbackToDestructiveMigration
     in the database builder (see MainActivity)*/
}