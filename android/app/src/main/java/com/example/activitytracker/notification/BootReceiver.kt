package com.example.activitytracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.activitytracker.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import android.util.Log
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationSetup(context).createNotificationChannel()
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reminders = AppDatabase.getInstance(context).reminderDao().getAllOnce()
                    val scheduler = AlarmScheduler(context)
                    scheduler.cancelLegacyAlarm()
                    reminders.forEach { scheduler.scheduleReminder(it) }
                } catch (e: Exception) { // we catch everything so the app doesn't crash
                    Log.e("BootReceiver", "Failed to reschedule reminders", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}