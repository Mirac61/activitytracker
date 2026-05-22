package com.example.activitytracker.widget

import android.content.Intent

object AddActivityWidgetIntentHandler {

    const val EXTRA_OPEN_ADD = "openAdd"

    fun shouldOpenAddActivity(intent: Intent?): Boolean {
        return intent?.getBooleanExtra(EXTRA_OPEN_ADD, false) ?: false
    }
}