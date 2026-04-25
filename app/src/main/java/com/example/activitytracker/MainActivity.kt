package com.example.activitytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room.Room
import com.example.activitytracker.Core.theme.ActivityTrackerTheme
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.ui.main.ActivityTrackerApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActivityTrackerTheme {
                ActivityTrackerApp()
            }
        }

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "activity_tracker_db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

        /* Temporary test to trigger database creation. Will be moved to ViewModel and Repository later.*/
        Thread {
            db.activityDao().getAll()
        }.start()
    }
}

