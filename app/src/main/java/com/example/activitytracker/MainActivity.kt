package com.example.activitytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.activitytracker.Core.theme.ActivityTrackerTheme
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
    }
}

