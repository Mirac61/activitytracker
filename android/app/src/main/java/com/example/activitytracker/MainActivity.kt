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
import com.example.activitytracker.notification.AlarmScheduler
import com.example.activitytracker.notification.NotificationHelper
import android.Manifest
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.activitytracker.notification.WeatherWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private var openAddActivityRequestId by mutableStateOf(0)

    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Benachrichtigungen deaktiviert", Toast.LENGTH_SHORT).show()
        }
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            startWeatherWorker() // Jetzt wird die Funktion gefunden
        } else {
            Toast.makeText(this, "Standort wird benötigt", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)}

        handleIntent(intent)

        setContent {
            ActivityTrackerTheme {
                ActivityTrackerApp(openAddActivityRequestId = openAddActivityRequestId)
            }
        }
        NotificationHelper(this).createNotificationChannel()
        AlarmScheduler(this).scheduleDailyAlarm()

        if (BuildConfig.DEBUG) {
            WorkManager.getInstance(this)
                .enqueueUniqueWork( // WeatherWorker bei jedem App neustart triggern
                    "TestWeather",
                    ExistingWorkPolicy.REPLACE,
                    OneTimeWorkRequestBuilder<WeatherWorker>().build()
                )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun startWeatherWorker() {
        val workRequest = PeriodicWorkRequestBuilder<WeatherWorker>(1, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WeatherWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun handleIntent(intent: Intent?) {
        val shouldOpenAdd = AddActivityWidgetIntentHandler.shouldOpenAddActivity(intent)

        if (shouldOpenAdd) {
            openAddActivityRequestId++
            intent?.removeExtra(AddActivityWidgetIntentHandler.EXTRA_OPEN_ADD)
        }
    }
}
