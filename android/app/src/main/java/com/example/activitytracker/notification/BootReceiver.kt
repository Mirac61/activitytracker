package com.example.activitytracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// starts the notification scheduler after a restart
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationHelper(context).createNotificationChannel()
            AlarmScheduler(context).scheduleDailyAlarm()
        }
    }
}