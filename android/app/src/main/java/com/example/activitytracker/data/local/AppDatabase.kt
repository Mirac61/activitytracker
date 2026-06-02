package com.example.activitytracker.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntity
import android.content.Context
import androidx.room.TypeConverters
import com.example.activitytracker.data.local.converter.Converters
import com.example.activitytracker.data.local.converter.SyncStatusConverter

@Database(entities = [ActivityEntity::class], version = 7)
@TypeConverters(Converters::class, SyncStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    // Refactoring to Singleton
    // Inspiration from https://medium.com/@stephenmuindi241/singleton-pattern-in-room-database-566c250196aa
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "activity_tracker_db"
                    )
                        // For development
                        .fallbackToDestructiveMigration(dropAllTables = true)
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}