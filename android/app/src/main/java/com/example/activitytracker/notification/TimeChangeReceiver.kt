package com.example.activitytracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.activitytracker.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED ||
            intent.action == "android.intent.action.TIMEZONE_OFFSET_CHANGED") {

            NotificationSetup(context).createNotificationChannel()
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reminders = AppDatabase.getInstance(context).reminderDao().getAllOnce()
                    val scheduler = AlarmScheduler(context)
                    scheduler.cancelLegacyAlarm()
                    reminders.forEach { scheduler.scheduleReminder(it) }
                } catch (e: Exception) { // we catch everything so the app doesn't crash
                    Log.e("TimeChangeReceiver", "Failed to reschedule reminders", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
