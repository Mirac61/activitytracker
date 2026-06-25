package com.example.activitytracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.activitytracker.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val requestCode = intent.getIntExtra("reminder_request_code", -1)
        if (requestCode == -1) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val reminder = AppDatabase.getInstance(context)
                    .reminderDao()
                    .getByRequestCode(requestCode)
                if (reminder != null) {
                    NotificationSetup(context).showNotification(
                        requestCode, CHANNEL_REMINDER_ID, reminder.title, reminder.text
                    )
                    AlarmScheduler(context).scheduleReminder(reminder)
                }
            } catch (e: Exception) { // we catch everything so the app doesn't crash
                Log.e("AlarmReceiver", "Failed to process alarm", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
