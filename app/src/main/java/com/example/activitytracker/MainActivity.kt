package com.example.activitytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.activitytracker.Core.theme.ActivityTrackerTheme
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.ui.main.ActivityTrackerApp
import com.example.activitytracker.ui.screens.tracking.TrackingViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActivityTrackerTheme {
                ActivityTrackerApp()
            }
        }


        //binding.newTas

//        // Temporary trigger to initialize the database. should be moved to a ViewModel
//        val db = AppDatabase.getInstance(applicationContext)
//
//        lifecycleScope.launch{
//            db.activityDao().getAll().firstOrNull()
//        }
    }
}
