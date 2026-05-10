package com.example.activitytracker

import android.content.Intent
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

class MainActivity : ComponentActivity() {
    private var openAddActivityRequestId by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            ActivityTrackerTheme {
                ActivityTrackerApp(openAddActivityRequestId = openAddActivityRequestId)
            }
        }
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
        }
    }
}
