package com.example.activitytracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import com.example.activitytracker.Core.theme.ActivityTrackerTheme
import com.example.activitytracker.notification.AlarmScheduler
import com.example.activitytracker.notification.NotificationHelper
import com.example.activitytracker.ui.main.ActivityTrackerApp

import android.Manifest


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        setContent {
            ActivityTrackerTheme {
                ActivityTrackerApp()
            }
        }
        NotificationHelper(this).createNotificationChannel()
        AlarmScheduler(this).scheduleDailyAlarm()
    }
}
