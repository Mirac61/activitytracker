package com.example.activitytracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED ||
            intent.action == "android.intent.action.TIMEZONE_OFFSET_CHANGED") {

            NotificationHelper(context).createNotificationChannel()
            AlarmScheduler(context).scheduleDailyAlarm()
        }
    }
}