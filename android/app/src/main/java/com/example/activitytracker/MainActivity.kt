package com.example.activitytracker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.activitytracker.Core.theme.ActivityTrackerTheme
import com.example.activitytracker.ui.main.ActivityTrackerApp
import com.example.activitytracker.widget.AddActivityWidgetIntentHandler
import androidx.core.app.ActivityCompat
import com.example.activitytracker.notification.AlarmScheduler
import com.example.activitytracker.notification.NotificationHelper
import android.Manifest

class MainActivity : ComponentActivity() {
    private var openAddActivityRequestId by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        setContent {
            ActivityTrackerTheme {
                ActivityTrackerApp(openAddActivityRequestId = openAddActivityRequestId)
            }
        }
        NotificationHelper(this).createNotificationChannel()
        AlarmScheduler(this).scheduleDailyAlarm()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val shouldOpenAdd = AddActivityWidgetIntentHandler.shouldOpenAddActivity(intent)

        if (shouldOpenAdd) {
            openAddActivityRequestId++
            intent?.removeExtra(AddActivityWidgetIntentHandler.EXTRA_OPEN_ADD)
        }
    }
}
